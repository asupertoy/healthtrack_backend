package com.healthtrack.repository;

import com.healthtrack.entity.MonthlySummary;
import com.healthtrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonthlySummaryRepository extends JpaRepository<MonthlySummary, Long> {

    List<MonthlySummary> findByUser(User user);
}
