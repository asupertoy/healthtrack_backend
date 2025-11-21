package com.healthtrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitation_id")
    private Long invitationId;

    @ManyToOne
    @JoinColumn(name = "inviter_user_id")
    @JsonIgnore
    private User inviter;

    @Column(name = "receiver_email", length = 30)
    private String receiverEmail;

    @Column(name = "receiver_phone", length = 32)
    private String receiverPhone;

    @Column(name = "related_type", length = 50)
    private String relatedType; // challenge, share, view 等

    @Column(name = "related_id")
    private Long relatedId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private InvitationStatus status = InvitationStatus.PENDING;

    @Column(name = "initiated_at")
    private LocalDateTime initiatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    public enum InvitationStatus {
        PENDING, ACCEPTED, EXPIRED, CANCELLED
    }

    @PrePersist
    protected void onCreate() {
        initiatedAt = LocalDateTime.now();
    }
}
