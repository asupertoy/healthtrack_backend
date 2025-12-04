package com.healthtrack.controller;

import com.healthtrack.entity.User;
import com.healthtrack.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 * Debug endpoints for local testing. Be careful: do not expose in production.
 */
@RestController
@RequestMapping("/internal/debug")
public class InternalDebugController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InternalDebugController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/password-verify")
    public ResponseEntity<?> verifyPassword(@RequestParam String phone, @RequestParam String plain) {
        Optional<User> opt = userRepository.findByPhone(phone);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        User u = opt.get();
        boolean matches = u.getPassword() != null && passwordEncoder.matches(plain, u.getPassword());
        return ResponseEntity.ok(Map.of("phone", phone, "matches", matches));
    }
}
