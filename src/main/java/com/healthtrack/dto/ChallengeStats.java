package com.healthtrack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeStats {
    private Long challengeId;
    private String title;
    private long participantCount;
    private long completedCount;
}

