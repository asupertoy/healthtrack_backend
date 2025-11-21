package com.healthtrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_provider_links",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "provider_id"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProviderLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "link_id")
    private Long linkId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    @JsonIgnore
    private Provider provider;

    @Column(name = "verification_status", length = 50)
    private String verificationStatus = "unverified";

    @Column(name = "linked_at")
    private LocalDateTime linkedAt;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    @PrePersist
    protected void onCreate() {
        linkedAt = LocalDateTime.now();
    }
}
