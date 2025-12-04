// src/main/java/com/healthtrack/controller/UserController.java
package com.healthtrack.controller;

import com.healthtrack.entity.User;
import com.healthtrack.entity.UserProviderLink;
import com.healthtrack.security.JwtTokenProvider;
import com.healthtrack.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // -------------------
    // User CRUD
    // -------------------
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // 修改：部分更新用户（PATCH），路径包含用户 id，body 为 User 对象（只会拷贝允许的字段，userId 和 createdAt 不可变）
    // After update, return new access/refresh tokens so client remains authenticated if key fields changed
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody User user) {
        // Load existing user and copy only allowed fields (exclude userId and createdAt)
        User existing = userService.getUserById(id);

        // Allowed to update: healthId, name, dateOfBirth, phone, phoneVerified,
        // phoneVerifiedAt, password, enabled, accountNonLocked, lastLoginAt
        if (user.getHealthId() != null) existing.setHealthId(user.getHealthId());
        if (user.getName() != null) existing.setName(user.getName());
        if (user.getDateOfBirth() != null) existing.setDateOfBirth(user.getDateOfBirth());
        if (user.getPhone() != null) existing.setPhone(user.getPhone());
        // If phone changed, clear verification status to avoid keeping verification from the old number
        boolean phoneChanged = false;
        if (user.getPhone() != null) {
            // compare before assigning to detect actual change
            if (!user.getPhone().equals(existing.getPhone())) {
                existing.setPhone(user.getPhone());
                existing.setPhoneVerified(false); // set to 0
                existing.setPhoneVerifiedAt(null);
                phoneChanged = true;
            }
        }

        // Only apply incoming verification fields when phone was NOT changed
        if (!phoneChanged) {
            if (user.getPhoneVerified() != null) existing.setPhoneVerified(user.getPhoneVerified());
            if (user.getPhoneVerifiedAt() != null) existing.setPhoneVerifiedAt(user.getPhoneVerifiedAt());
        }
        if (user.getPassword() != null && !user.getPassword().isBlank()) existing.setPassword(user.getPassword());
        if (user.getEnabled() != null) existing.setEnabled(user.getEnabled());
        if (user.getAccountNonLocked() != null) existing.setAccountNonLocked(user.getAccountNonLocked());
        if (user.getLastLoginAt() != null) existing.setLastLoginAt(user.getLastLoginAt());

        // Note: do NOT copy userId or createdAt; relations (emails, providerLinks, etc.) are also left unchanged

        User saved = userService.updateUser(existing);

        // Re-issue tokens using immutable userId as subject
        String accessToken = jwtTokenProvider.generateAccessToken(saved);
        String refreshToken = jwtTokenProvider.generateRefreshToken(saved);

        Map<String, Object> resp = new HashMap<>();
        resp.put("user", saved);
        resp.put("accessToken", accessToken);
        resp.put("refreshToken", refreshToken);
        resp.put("tokenType", "Bearer");

        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // -------------------
    // UserProviderLink 子模块接口
    // -------------------
    @PostMapping("/{userId}/provider-links")
    public ResponseEntity<UserProviderLink> addUserProviderLink(
            @PathVariable Long userId,
            @RequestParam Long providerId,
            @RequestParam boolean isPrimary) {
        return ResponseEntity.ok(userService.addUserProviderLink(userId, providerId, isPrimary));
    }

    @GetMapping("/{userId}/provider-links")
    public ResponseEntity<List<UserProviderLink>> getUserProviderLinks(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProviderLinks(userId));
    }

    @DeleteMapping("/provider-links/{id}")
    public ResponseEntity<Void> deleteUserProviderLink(@PathVariable Long id) {
        userService.deleteUserProviderLink(id);
        return ResponseEntity.ok().build();
    }
}
