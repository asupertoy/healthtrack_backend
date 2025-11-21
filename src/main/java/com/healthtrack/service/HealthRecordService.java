package com.healthtrack.service;

import com.healthtrack.entity.HealthRecord;
import com.healthtrack.entity.MonthlySummary;
import com.healthtrack.entity.User;
import com.healthtrack.repository.HealthRecordRepository;
import com.healthtrack.repository.MonthlySummaryRepository;
import com.healthtrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public HealthRecord updateRecord(HealthRecord record) {
        return healthRecordRepository.save(record);
    }

    public void deleteRecord(Long recordId) {
        healthRecordRepository.deleteById(recordId);
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
