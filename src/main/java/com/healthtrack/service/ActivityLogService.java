package com.healthtrack.service;

import com.healthtrack.entity.ActivityLog;
import com.healthtrack.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository repository;

    public ActivityLogService(ActivityLogRepository repository) {
        this.repository = repository;
    }

    public ActivityLog save(ActivityLog log) {
        return repository.save(log);
    }

    public List<ActivityLog> getByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

}
