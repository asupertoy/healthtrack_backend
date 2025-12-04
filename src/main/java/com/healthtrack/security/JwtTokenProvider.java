package com.healthtrack.security;

import com.healthtrack.entity.User;
import com.healthtrack.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenProvider {

    private final UserRepository userRepository;

    private final String secret;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    private Key key;

    public JwtTokenProvider(UserRepository userRepository,
                            @Value("${security.jwt.secret}") String secret,
                            @Value("${security.jwt.access-token-expiration}") long accessTokenExpirationMs,
                            @Value("${security.jwt.refresh-token-expiration}") long refreshTokenExpirationMs) {
        this.userRepository = userRepository;
        this.secret = secret;
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
        initKey();
    }

    private void initKey() {
        // Use the raw UTF-8 bytes of the secret instead of Base64 decoding
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationMs);

        // Use immutable userId as subject to avoid issues when healthId/phone/email change
        return Jwts.builder()
                .setSubject(String.valueOf(user.getUserId()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("type", "ACCESS")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
                .setSubject(String.valueOf(user.getUserId()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("type", "REFRESH")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String getSubject(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public Authentication getAuthentication(String token) {
        String subject = getSubject(token);
        Optional<User> userOpt = Optional.empty();
        // Try to parse subject as userId first (new tokens). If parsing fails, fall back to healthId for backward compatibility.
        try {
            long userId = Long.parseLong(subject);
            userOpt = userRepository.findById(userId);
        } catch (NumberFormatException ex) {
            // fall back to previous behavior: subject was healthId
            userOpt = userRepository.findByHealthId(subject);
        }

        if (userOpt.isEmpty()) {
            return null;
        }
        User user = userOpt.get();
        // Currently we don't have roles, so grant a default ROLE_USER
        return new UsernamePasswordAuthenticationToken(user, null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
