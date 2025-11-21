// src/main/java/com/healthtrack/controller/UserController.java
package com.healthtrack.controller;

import com.healthtrack.entity.User;
import com.healthtrack.entity.UserProviderLink;
import com.healthtrack.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
