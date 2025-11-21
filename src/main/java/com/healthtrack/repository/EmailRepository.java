// src/main/java/com/healthtrack/repository/EmailRepository.java
package com.healthtrack.repository;

import com.healthtrack.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    // 只需要这个查询方法
    Optional<Email> findByEmailAddress(String emailAddress);

    // 可选：查询已验证/未验证的邮箱
    List<Email> findByVerifiedTrue();
    List<Email> findByVerifiedFalse();
}