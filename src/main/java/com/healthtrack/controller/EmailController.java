// src/main/java/com/healthtrack/controller/EmailController.java
package com.healthtrack.controller;

import com.healthtrack.entity.Email;
import com.healthtrack.repository.EmailRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private final EmailRepository emailRepository;

    public EmailController(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        Email email = emailRepository.findByVerificationToken(token);
        if (email != null) {
            email.setVerified(true);
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
