package com.healthtrack.service;

import com.healthtrack.dto.AuthResponse;
import com.healthtrack.dto.LoginRequest;
import com.healthtrack.dto.RefreshTokenRequest;
import com.healthtrack.entity.Email;
import com.healthtrack.entity.User;
import com.healthtrack.repository.EmailRepository;
import com.healthtrack.repository.UserRepository;
import com.healthtrack.security.JwtTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       EmailRepository emailRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user;
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            user = userRepository.findByPhone(request.getPhone())
                    .orElseThrow(() -> new BadCredentialsException("Invalid phone or password"));
        } else if (request.getEmail() != null && !request.getEmail().isBlank()) {
            Email email = emailRepository.findByEmailAddress(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
            user = email.getUser();
        } else {
            throw new IllegalArgumentException("Phone or email is required");
        }

        if (!Boolean.TRUE.equals(user.getEnabled()) || !Boolean.TRUE.equals(user.getAccountNonLocked())) {
            throw new IllegalStateException("User is disabled or locked");
        }

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        user.setLastLoginAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getUserId())
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String subject = jwtTokenProvider.getSubject(token);

        Optional<User> userOpt = Optional.empty();
        try {
            long userId = Long.parseLong(subject);
            userOpt = userRepository.findById(userId);
        } catch (NumberFormatException ex) {
            userOpt = userRepository.findByHealthId(subject);
        }

        User user = userOpt.orElseThrow(() -> new IllegalArgumentException("User not found for refresh token"));

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .userId(user.getUserId())
                .build();
    }
}
