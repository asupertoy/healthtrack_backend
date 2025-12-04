package com.healthtrack.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateEmailRequest {
    private Long userId; // 关联的用户ID，可选
    private String emailAddress;
    private Boolean verified;
    private LocalDateTime verifiedAt;
}

