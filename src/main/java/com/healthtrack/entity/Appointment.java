package com.healthtrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @ManyToOne
    @JoinColumn(name = "booking_user_id")
    @JsonIgnore
    private User bookingUser;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    @JsonIgnore
    private Provider provider;

    @Column(name = "provider_email", length = 191)
    private String providerEmail;

    @Column(name = "provider_license", length = 20)
    private String providerLicense;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Convert(converter = AppointmentConsultationTypeConverter.class)
    @Column(name = "consultation_type", length = 20, nullable = false)
    private ConsultationType consultationType;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private AppointmentStatus status = AppointmentStatus.scheduled;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", length = 255)
    private String cancellationReason;

    public enum ConsultationType {
        IN_PERSON("In-Person"),
        VIRTUAL("Virtual");

        private final String dbValue;

        ConsultationType(String dbValue) {
            this.dbValue = dbValue;
        }

        public String getDbValue() {
            return dbValue;
        }

        public static ConsultationType fromDbValue(String dbValue) {
            for (ConsultationType type : values()) {
                if (type.dbValue.equalsIgnoreCase(dbValue)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown consultation type: " + dbValue);
        }
    }

    public enum AppointmentStatus {
        scheduled, cancelled, completed, no_show
    }
}
