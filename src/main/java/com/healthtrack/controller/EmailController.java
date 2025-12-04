// src/main/java/com/healthtrack/controller/EmailController.java
package com.healthtrack.controller;

import com.healthtrack.dto.CreateEmailRequest;
import com.healthtrack.entity.Email;
import com.healthtrack.entity.User;
import com.healthtrack.repository.EmailRepository;
import com.healthtrack.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private final EmailRepository emailRepository;
    private final UserRepository userRepository;

    public EmailController(EmailRepository emailRepository, UserRepository userRepository) {
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String emailAddress) {
        Optional<Email> emailOpt = emailRepository.findByEmailAddress(emailAddress);
        if (emailOpt.isPresent()) {
            Email email = emailOpt.get();
            email.setVerified(true);
            email.setVerifiedAt(LocalDateTime.now());
            emailRepository.save(email);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 取消验证（如果需要）
    @PostMapping("/unverify")
    public ResponseEntity<?> unverifyEmail(@RequestParam String emailAddress) {
        Optional<Email> emailOpt = emailRepository.findByEmailAddress(emailAddress);
        if (emailOpt.isPresent()) {
            Email email = emailOpt.get();
            email.setVerified(false);
            email.setVerifiedAt(null);
            emailRepository.save(email);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 使用简洁的 CreateEmailRequest 创建邮箱记录
    @PostMapping("/create")
    public ResponseEntity<Email> createEmail(@RequestBody CreateEmailRequest req) {
        Email email = new Email();
        email.setEmailAddress(req.getEmailAddress());
        email.setVerified(req.getVerified() != null ? req.getVerified() : false);
        if (req.getVerified() != null && req.getVerified() && req.getVerifiedAt() == null) {
            email.setVerifiedAt(LocalDateTime.now());
        } else {
            email.setVerifiedAt(req.getVerifiedAt());
        }

        if (req.getUserId() != null) {
            User user = userRepository.findById(req.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            email.setUser(user);
        }

        Email saved = emailRepository.save(email);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/by-user")
    public ResponseEntity<List<Email>> getEmailsByUser(@RequestParam Long userId) {
        List<Email> emails = emailRepository.findByUser_UserId(userId);
        return ResponseEntity.ok(emails);
    }

    // 部分更新邮箱记录（只允许修改 emailAddress、verified、verifiedAt）
    @PatchMapping("/{id}")
    public ResponseEntity<Email> updateEmail(@PathVariable Long id, @RequestBody Email update) {
        Email existing = emailRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Email not found"));

        // update email address with uniqueness check
        if (update.getEmailAddress() != null && !update.getEmailAddress().equals(existing.getEmailAddress())) {
            Optional<Email> conflict = emailRepository.findByEmailAddress(update.getEmailAddress());
            if (conflict.isPresent() && !conflict.get().getEmailId().equals(id)) {
                throw new IllegalArgumentException("Email address already in use");
            }
            existing.setEmailAddress(update.getEmailAddress());
        }

        // update verified flag and verifiedAt accordingly
        if (update.getVerified() != null) {
            existing.setVerified(update.getVerified());
            if (update.getVerified()) {
                // if verifiedAt provided use it, otherwise set now
                existing.setVerifiedAt(update.getVerifiedAt() != null ? update.getVerifiedAt() : LocalDateTime.now());
            } else {
                existing.setVerifiedAt(null);
            }
        } else if (update.getVerifiedAt() != null) {
            // allow admin to set verifiedAt without changing verified flag
            existing.setVerifiedAt(update.getVerifiedAt());
        }

        // Do NOT modify createdAt or user relation here
        Email saved = emailRepository.save(existing);
        return ResponseEntity.ok(saved);
    }

    // 删除邮箱记录
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmail(@PathVariable Long id) {
        if (!emailRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        emailRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}