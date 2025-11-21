package com.healthtrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "record_date")
    private LocalDate recordDate;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @Column(name = "metric_type", length = 100)
    private String metricType;

    @Column(name = "metric_value")
    private Double metricValue;

    @Column(name = "metric_json", columnDefinition = "JSON")
    private String metricJson;

    @Column(name = "metric_unit", length = 64)
    private String metricUnit;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        recordedAt = LocalDateTime.now();
    }
}
