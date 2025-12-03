package com.healthtrack.repository;

import com.healthtrack.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // 根据 booking_user_id 查询预约
    List<Appointment> findByBookingUser_UserId(Long bookingUserId);

    // 根据 provider_id 查询预约
    List<Appointment> findByProvider_ProviderId(Long providerId);
}
