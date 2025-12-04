package com.healthtrack.controller;

import com.healthtrack.entity.HealthRecord;
import com.healthtrack.entity.MonthlySummary;
import com.healthtrack.service.HealthRecordService;
import com.healthtrack.dto.HealthRecordLeaderboardEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/health-records")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    // 简单解析 yyyy-MM-dd 格式为 LocalDate，空或空字符串返回 null
    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid date format: " + value + ". Use yyyy-MM-dd");
        }
    }

    // -----------------------
    // HealthRecord 主模块
    // -----------------------
    @PostMapping("/{userId}")
    public HealthRecord createRecord(
            @PathVariable Long userId,
            @RequestBody HealthRecord record) {
        return healthRecordService.createRecord(userId, record);
    }

    @GetMapping("/{userId}")
    public List<HealthRecord> getRecordsByUser(@PathVariable Long userId) {
        return healthRecordService.getRecordsByUser(userId);
    }

    // 搜索接口：按用户 + 可选日期范围（recordDate）
    // GET /api/health-records/search?userId=1&startDate=2025-11-01&endDate=2025-11-30
    @GetMapping("/search")
    public List<HealthRecord> searchRecords(
            @RequestParam Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LocalDate start = parseDate(startDate);
        LocalDate end = parseDate(endDate);
        return healthRecordService.searchRecords(userId, start, end);
    }

    @PutMapping
    public HealthRecord updateRecord(@RequestBody HealthRecord record) {
        return healthRecordService.updateRecord(record);
    }

    @DeleteMapping("/{recordId}")
    public void deleteRecord(@PathVariable Long recordId) {
        healthRecordService.deleteRecord(recordId);
    }

    // 健康记录排行榜：按记录数倒序返回前 top 个用户
    @GetMapping("/leaderboard")
    public List<HealthRecordLeaderboardEntry> getLeaderboard(
            @RequestParam(name = "top", defaultValue = "10") int top) {
        return healthRecordService.getLeaderboard(top);
    }

    // -----------------------
    // MonthlySummary 子模块
    // -----------------------
    @PostMapping("/summary/{userId}")
    public MonthlySummary addMonthlySummary(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestBody String metricsJson) {
        return healthRecordService.addMonthlySummary(userId, year, month, metricsJson);
    }

    @GetMapping("/summary/{userId}")
    public List<MonthlySummary> getMonthlySummaries(@PathVariable Long userId) {
        return healthRecordService.getMonthlySummaries(userId);
    }
}
