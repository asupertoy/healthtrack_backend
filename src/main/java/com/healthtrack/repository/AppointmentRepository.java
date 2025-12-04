package com.healthtrack.repository;

import com.healthtrack.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // 根据 booking_user_id 查询预约
    List<Appointment> findByBookingUser_UserId(Long bookingUserId);

    // 根据 provider_id 查询预约
    List<Appointment> findByProvider_ProviderId(Long providerId);

    // 综合查询：可选按用户、提供者、时间范围过滤
    @Query("select a from Appointment a " +
           "where (:bookingUserId is null or a.bookingUser.userId = :bookingUserId) " +
           "and (:providerId is null or a.provider.providerId = :providerId) " +
           "and (:startDate is null or a.scheduledAt >= :startDate) " +
           "and (:endDate is null or a.scheduledAt <= :endDate)")
    List<Appointment> searchAppointments(@Param("bookingUserId") Long bookingUserId,
                                         @Param("providerId") Long providerId,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);
}
