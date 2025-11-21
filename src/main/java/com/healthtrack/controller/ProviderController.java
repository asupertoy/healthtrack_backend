// src/main/java/com/healthtrack/controller/ProviderController.java
package com.healthtrack.controller;

import com.healthtrack.entity.Provider;
import com.healthtrack.repository.ProviderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {

    private final ProviderRepository providerRepository;

    public ProviderController(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @GetMapping
    public ResponseEntity<List<Provider>> getAll() {
        return ResponseEntity.ok(providerRepository.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<Provider> create(@RequestBody Provider provider) {
        return ResponseEntity.ok(providerRepository.save(provider));
    }
}
