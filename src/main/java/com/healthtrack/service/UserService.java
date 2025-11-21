package com.healthtrack.service;

import com.healthtrack.entity.MonthlySummary;
import com.healthtrack.entity.User;
import com.healthtrack.entity.UserProviderLink;
import com.healthtrack.entity.Provider;
import com.healthtrack.repository.MonthlySummaryRepository;
import com.healthtrack.repository.UserProviderLinkRepository;
import com.healthtrack.repository.UserRepository;
import com.healthtrack.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
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

    // =====================
    // User 相关操作
    // =====================

    public User createUser(User user) {
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
}
