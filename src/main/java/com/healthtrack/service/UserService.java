package com.healthtrack.service;

import com.healthtrack.dto.RegisterRequest;
import com.healthtrack.entity.Email;
import com.healthtrack.entity.MonthlySummary;
import com.healthtrack.entity.User;
import com.healthtrack.entity.UserProviderLink;
import com.healthtrack.entity.Provider;
import com.healthtrack.repository.MonthlySummaryRepository;
import com.healthtrack.repository.UserProviderLinkRepository;
import com.healthtrack.repository.UserRepository;
import com.healthtrack.repository.ProviderRepository;
import com.healthtrack.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProviderLinkRepository userProviderLinkRepository;
    private final ProviderRepository providerRepository;
    private final MonthlySummaryRepository monthlySummaryRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailRepository emailRepository;

    // =====================
    // User 相关操作
    // =====================

    public User createUser(User user) {
        // 业务层先查后插，避免直接触发数据库唯一约束异常
        if (user.getHealthId() != null) {
            userRepository.findByHealthId(user.getHealthId()).ifPresent(existing -> {
                throw new IllegalArgumentException("健康档案号已存在: " + user.getHealthId());
            });
        }
        if (user.getPhone() != null) {
            userRepository.findByPhone(user.getPhone()).ifPresent(existing -> {
                throw new IllegalArgumentException("手机号已被使用: " + user.getPhone());
            });
        }

        // 如果前端传入了原始密码，这里进行加密存储
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // =====================
    // UserProviderLink 操作（父Service-子模块）
    // =====================
    @Transactional
    public UserProviderLink addUserProviderLink(Long userId, Long providerId, boolean isPrimary) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        // 如果 isPrimary，检查是否已有 primary
        if (isPrimary) {
            userProviderLinkRepository.findByUserAndIsPrimaryTrue(user)
                    .ifPresent(link -> {
                        throw new RuntimeException("User already has a primary provider");
                    });
        }

        UserProviderLink link = new UserProviderLink();
        link.setUser(user);
        link.setProvider(provider);
        link.setIsPrimary(isPrimary);

        return userProviderLinkRepository.save(link);
    }

    public List<UserProviderLink> getUserProviderLinks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userProviderLinkRepository.findByUser(user);
    }

    @Transactional
    public void deleteUserProviderLink(Long linkId) {
        // 直接通过关联记录ID删除，更高效
        if (!userProviderLinkRepository.existsById(linkId)) {
            throw new RuntimeException("Provider link not found with id: " + linkId);
        }
        userProviderLinkRepository.deleteById(linkId);
    }

    // =====================
    // MonthlySummary 操作（父Service-子模块）
    // =====================
    public MonthlySummary addMonthlySummary(Long userId, int year, int month, String healthMetricsJson) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MonthlySummary summary = new MonthlySummary();
        summary.setUser(user);
        summary.setYear(year);
        summary.setMonth(month);
        summary.setHealthMetrics(healthMetricsJson);

        return monthlySummaryRepository.save(summary);
    }

    public List<MonthlySummary> getMonthlySummaries(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return monthlySummaryRepository.findByUser(user);
    }

    // =====================
    // 注册相关操作
    // =====================
    @Transactional
    public User register(RegisterRequest request) {
        // 唯一性校验
        if (request.getHealthId() != null) {
            userRepository.findByHealthId(request.getHealthId()).ifPresent(existing -> {
                throw new IllegalArgumentException("健康档案号已存在: " + request.getHealthId());
            });
        }
        if (request.getPhone() != null) {
            userRepository.findByPhone(request.getPhone()).ifPresent(existing -> {
                throw new IllegalArgumentException("手机号已被使用: " + request.getPhone());
            });
        }

        // 创建用户并加密密码
        User user = User.builder()
                .healthId(request.getHealthId())
                .name(request.getName())
                .phone(request.getPhone())
                .enabled(true)
                .accountNonLocked(true)
                .build();

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User saved = userRepository.save(user);

        // 如果传了邮箱，创建 Email 记录并关联
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            Email email = new Email();
            email.setUser(saved);
            email.setEmailAddress(request.getEmail());
            email.setVerified(false);
            emailRepository.save(email);
        }

        return saved;
    }
}
