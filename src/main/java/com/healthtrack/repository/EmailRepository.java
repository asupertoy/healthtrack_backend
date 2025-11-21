// src/main/java/com/healthtrack/repository/EmailRepository.java
package com.healthtrack.repository;

import com.healthtrack.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    Email findByVerificationToken(String token);
}
