package com.healthtrack.repository;

import com.healthtrack.entity.HealthRecord;
import com.healthtrack.entity.User;
import com.healthtrack.dto.HealthRecordLeaderboardEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.time.LocalDate;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    List<HealthRecord> findByUser(User user);

    // 聚合统计每个用户的健康记录数量，按记录数倒序返回前 N 名
    @Query("select new com.healthtrack.dto.HealthRecordLeaderboardEntry(u.userId, u.healthId, u.name, count(r)) " +
           "from HealthRecord r join r.user u " +
           "group by u.userId, u.healthId, u.name " +
           "order by count(r) desc")
    List<HealthRecordLeaderboardEntry> findLeaderboard(Pageable pageable);

    // 按用户 + 可选日期范围搜索健康记录
    @Query("select r from HealthRecord r " +
           "where r.user = :user " +
           "and (:startDate is null or r.recordDate >= :startDate) " +
           "and (:endDate is null or r.recordDate <= :endDate)")
    List<HealthRecord> searchByUserAndDateRange(User user, LocalDate startDate, LocalDate endDate);
}
