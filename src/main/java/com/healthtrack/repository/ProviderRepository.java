// src/main/java/com/healthtrack/repository/ProviderRepository.java
package com.healthtrack.repository;

import com.healthtrack.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
}
