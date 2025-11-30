package com.healthtrack.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String healthId;
    private String name;
    private String phone;
    private String email;   // 可选邮箱
    private String password;
}

