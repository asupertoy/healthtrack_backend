package com.healthtrack.controller;

import com.healthtrack.entity.HealthRecord;
import com.healthtrack.entity.MonthlySummary;
import com.healthtrack.service.HealthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-records")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

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

    @PutMapping
    public HealthRecord updateRecord(@RequestBody HealthRecord record) {
        return healthRecordService.updateRecord(record);
    }

    @DeleteMapping("/{recordId}")
    public void deleteRecord(@PathVariable Long recordId) {
        healthRecordService.deleteRecord(recordId);
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
