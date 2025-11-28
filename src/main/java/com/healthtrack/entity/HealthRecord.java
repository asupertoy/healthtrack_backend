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
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "recorded_at", insertable = false, updatable = false)
    private LocalDateTime recordedAt;

    @Column(name = "metric_type", length = 100, nullable = false)
    private String metricType;

    @Column(name = "metric_value")
    private Double metricValue;

    @Column(name = "metric_json", columnDefinition = "JSON")
    private String metricJson;

    @Column(name = "metric_unit", length = 64)
    private String metricUnit;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}
