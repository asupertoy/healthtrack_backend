package com.healthtrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "emails")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_id")
    private Long emailId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user; // nullable per schema

    // emails.email VARCHAR(191) UNIQUE NOT NULL
    @Column(name = "email", nullable = false, unique = true, length = 191)
    private String emailAddress;

    @Column(name = "verified")
    private Boolean verified = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    // remove @PrePersist, DB default CURRENT_TIMESTAMP will populate created_at
}
