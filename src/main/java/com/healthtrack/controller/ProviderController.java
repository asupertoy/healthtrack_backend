// src/main/java/com/healthtrack/controller/ProviderController.java
package com.healthtrack.controller;

import com.healthtrack.entity.Provider;
import com.healthtrack.repository.ProviderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/{id}")
    public ResponseEntity<Provider> getById(@PathVariable Long id) {
        Optional<Provider> opt = providerRepository.findById(id);
        return opt.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Provider> create(@RequestBody Provider provider) {
        return ResponseEntity.ok(providerRepository.save(provider));
    }
}
