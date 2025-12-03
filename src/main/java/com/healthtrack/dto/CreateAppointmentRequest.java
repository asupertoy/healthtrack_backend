package com.healthtrack.dto;

import com.healthtrack.entity.Appointment;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 前端创建预约时使用的请求体 DTO
 */
@Data
public class CreateAppointmentRequest {

    private Long bookingUserId;

    private Long providerId;

    private LocalDateTime scheduledAt;

    private Appointment.ConsultationType consultationType;

    private String memo;
}

