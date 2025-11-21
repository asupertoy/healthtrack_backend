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
    @JoinColumn(name = "challenge_id")
    @JsonIgnore
    private Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "invited_contact_email", length = 30)
    private String invitedContactEmail;

    @Column(name = "invited_contact_phone", length = 32)
    private String invitedContactPhone;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "current_progress", columnDefinition = "JSON")
    private String currentProgress;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ParticipantStatus status = ParticipantStatus.INVITED;

    public enum ParticipantStatus {
        INVITED, JOINED, LEFT, COMPLETED
    }

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }
}
