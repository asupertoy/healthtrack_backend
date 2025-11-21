// src/main/java/com/healthtrack/controller/EmailController.java
package com.healthtrack.controller;

import com.healthtrack.entity.Email;
import com.healthtrack.repository.EmailRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private final EmailRepository emailRepository;

    public EmailController(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    // ✅ 直接验证邮箱（无需令牌）
    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String emailAddress) {
        Optional<Email> emailOpt = emailRepository.findByEmailAddress(emailAddress);
        if (emailOpt.isPresent()) {
            Email email = emailOpt.get();
            email.setVerified(true);
            email.setVerifiedAt(java.time.LocalDateTime.now());
            emailRepository.save(email);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ✅ 取消验证（如果需要）
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

    @PostMapping("/create")
    public ResponseEntity<Email> createEmail(@RequestBody Email email) {
        return ResponseEntity.ok(emailRepository.save(email));
    }
}