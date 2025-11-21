// src/main/java/com/healthtrack/repository/InvitationRepository.java
package com.healthtrack.repository;

import com.healthtrack.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
}
