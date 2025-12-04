package com.healthtrack.service;

import com.healthtrack.entity.HealthRecord;
import com.healthtrack.entity.MonthlySummary;
import com.healthtrack.entity.User;
import com.healthtrack.dto.HealthRecordLeaderboardEntry;
import com.healthtrack.repository.HealthRecordRepository;
import com.healthtrack.repository.MonthlySummaryRepository;
import com.healthtrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final UserRepository userRepository;
    private final MonthlySummaryRepository monthlySummaryRepository;

    // -----------------------
    // HealthRecord 主模块
    // -----------------------
    public HealthRecord createRecord(Long userId, HealthRecord record) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        record.setUser(user);
        return healthRecordRepository.save(record);
    }

    public List<HealthRecord> getRecordsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return healthRecordRepository.findByUser(user);
    }

    // 按用户 + 可选日期范围搜索健康记录
    public List<HealthRecord> searchRecords(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return healthRecordRepository.searchByUserAndDateRange(user, startDate, endDate);
    }

    public HealthRecord updateRecord(HealthRecord record) {
        return healthRecordRepository.save(record);
    }

    public void deleteRecord(Long recordId) {
        healthRecordRepository.deleteById(recordId);
    }

    // 排行榜：按健康记录数量倒序返回前 N 个用户
    public List<HealthRecordLeaderboardEntry> getLeaderboard(int top) {
        int size = top <= 0 ? 10 : top; // 简单保护，非法值回退为默认 10
        // 可选：限制最大值，避免查询过多记录
        if (size > 100) {
            size = 100;
        }
        return healthRecordRepository.findLeaderboard(PageRequest.of(0, size));
    }

    // -------------------------------------------------
    // MonthlySummary 子模块（HealthRecord → Summary）
    // -------------------------------------------------
    @Transactional
    public MonthlySummary addMonthlySummary(
            Long userId, int year, int month, String metricsJson) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MonthlySummary summary = new MonthlySummary();
        summary.setUser(user);
        summary.setYear(year);
        summary.setMonth(month);
        summary.setHealthMetrics(metricsJson);

        return monthlySummaryRepository.save(summary);
    }

    public List<MonthlySummary> getMonthlySummaries(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return monthlySummaryRepository.findByUser(user);
    }
}
