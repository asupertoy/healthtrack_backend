package com.healthtrack.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用于取消预约时的请求体，只包含需要更新的字段
 */
@Data
public class CancelAppointmentRequest {

    private LocalDateTime cancelledAt;

    private String cancellationReason;
}

