// src/main/java/com/healthtrack/controller/InvitationController.java
package com.healthtrack.controller;

import com.healthtrack.entity.Invitation;
import com.healthtrack.repository.InvitationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationRepository invitationRepository;

    public InvitationController(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    @GetMapping
    public ResponseEntity<List<Invitation>> getAll() {
        return ResponseEntity.ok(invitationRepository.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<Invitation> create(@RequestBody Invitation invitation) {
        return ResponseEntity.ok(invitationRepository.save(invitation));
    }
}
