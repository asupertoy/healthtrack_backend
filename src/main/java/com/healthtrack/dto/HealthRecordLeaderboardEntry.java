package com.healthtrack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthRecordLeaderboardEntry {
    private Long userId;
    private String healthId;
    private String name;
    private Long recordCount;
}

