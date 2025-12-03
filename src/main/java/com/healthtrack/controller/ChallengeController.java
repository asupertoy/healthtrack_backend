// src/main/java/com/healthtrack/controller/ChallengeController.java
package com.healthtrack.controller;

import com.healthtrack.dto.ChallengeStats;
import com.healthtrack.entity.Challenge;
import com.healthtrack.entity.ChallengeParticipant;
import com.healthtrack.service.ChallengeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @PostMapping("/create")
    public ResponseEntity<Challenge> create(@RequestBody Challenge challenge) {
        return ResponseEntity.ok(challengeService.createChallenge(challenge));
    }

    @GetMapping
    public ResponseEntity<List<Challenge>> getAll() {
        return ResponseEntity.ok(challengeService.getAllChallenges());
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ChallengeStats>> getStats() {
        return ResponseEntity.ok(challengeService.getAllChallengeStats());
    }

    // 查询某个用户参与的挑战
    @GetMapping("/by-user")
    public ResponseEntity<List<Challenge>> getByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(challengeService.getChallengesByUser(userId));
    }

    // 查询某个用户尚未参加的挑战
    @GetMapping("/not-joined-by-user")
    public ResponseEntity<List<Challenge>> getNotJoinedByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(challengeService.getChallengesNotJoinedByUser(userId));
    }

    // -------------------
    // ChallengeParticipant 子模块接口
    // -------------------

    @PostMapping("/{challengeId}/participants/{userId}")
    public ChallengeParticipant addParticipant(
            @PathVariable Long challengeId,
            @PathVariable Long userId) {
        return challengeService.addParticipant(challengeId, userId);
    }

    @GetMapping("/{challengeId}/participants")
    public List<ChallengeParticipant> getParticipants(@PathVariable Long challengeId) {
        return challengeService.getParticipants(challengeId);
    }

    @DeleteMapping("/{challengeId}/participants/{userId}")
    public void removeParticipant(@PathVariable Long challengeId, @PathVariable Long userId) {
        challengeService.removeParticipant(challengeId, userId);
    }

    @PutMapping("/{challengeId}/participants/{userId}/status")
    public ChallengeParticipant updateParticipantStatus(
            @PathVariable Long challengeId,
            @PathVariable Long userId,
            @RequestParam ChallengeParticipant.ParticipantStatus status) {
        return challengeService.updateParticipantStatus(challengeId, userId, status);
    }
}
