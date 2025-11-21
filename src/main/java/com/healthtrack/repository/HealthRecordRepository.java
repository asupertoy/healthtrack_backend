package com.healthtrack.repository;

import com.healthtrack.entity.HealthRecord;
import com.healthtrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    List<HealthRecord> findByUser(User user);
}
