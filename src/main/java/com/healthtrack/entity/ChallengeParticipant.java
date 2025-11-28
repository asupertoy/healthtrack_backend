package com.healthtrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_id")
    private Long participationId;

    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = false)
    @JsonIgnore
    private Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "invited_contact_email", length = 191)
    private String invitedContactEmail;

    @Column(name = "invited_contact_phone", length = 32)
    private String invitedContactPhone;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "current_progress", columnDefinition = "JSON")
    private String currentProgress;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private ParticipantStatus status = ParticipantStatus.invited;

    public enum ParticipantStatus {
        invited, joined, left, completed
    }

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }
}
