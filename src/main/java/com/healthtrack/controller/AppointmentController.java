package com.healthtrack.controller;

import com.healthtrack.dto.CancelAppointmentRequest;
import com.healthtrack.dto.CreateAppointmentRequest;
import com.healthtrack.entity.Appointment;
import com.healthtrack.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // 简单的日期/日期时间解析工具：支持多种常见格式
    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        // 1) 尝试解析无时区的 LocalDateTime（如 2025-11-01T00:00:00 或 2025-11-01T00:00:00.000）
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException ignored) {
            // continue to next strategy
        }
        // 2) 尝试带偏移/时区的 ISO-8601（如 2025-10-31T16:00:00.000Z 或 +08:00），转换到系统默认时区
        try {
            OffsetDateTime odt = OffsetDateTime.parse(value);
            return odt.toLocalDateTime();
        } catch (DateTimeParseException ignored) {
            // continue
        }
        try {
            ZonedDateTime zdt = ZonedDateTime.parse(value);
            return zdt.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
            // continue
        }
        // 3) 尝试毫秒时间戳（纯数字字符串）
        if (value.chars().allMatch(Character::isDigit)) {
            try {
                long epochMillis = Long.parseLong(value);
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
            } catch (NumberFormatException ignored) {
                // continue
            }
        }
        // 4) 尝试仅日期格式 yyyy-MM-dd，自动补 00:00:00
        try {
            LocalDate date = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
            return date.atStartOfDay();
        } catch (DateTimeParseException ignored) {
            throw new IllegalArgumentException("Invalid date format: " + value +
                    ". Use yyyy-MM-dd, yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX], or epoch millis");
        }
    }

    @PostMapping
    public ResponseEntity<Appointment> create(@RequestBody CreateAppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.createAppointment(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> get(@PathVariable Long id) {
        Optional<Appointment> appointmentOpt = appointmentService.getAppointment(id);
        return appointmentOpt.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> getAll() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    // 综合搜索接口：GET /api/appointments/search?bookingUserId=&providerId=&startDate=&endDate=
    @GetMapping("/search")
    public ResponseEntity<List<Appointment>> search(
            @RequestParam(required = false) Long bookingUserId,
            @RequestParam(required = false) Long providerId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDateTime start = parseDateTime(startDate);
        LocalDateTime end = parseDateTime(endDate);

        List<Appointment> result = appointmentService.searchAppointments(bookingUserId, providerId, start, end);
        return ResponseEntity.ok(result);
    }

    // 根据 bookingUserId 查询该用户的所有预约
    @GetMapping("/by-booking-user")
    public ResponseEntity<List<Appointment>> getByBookingUser(@RequestParam Long bookingUserId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByBookingUser(bookingUserId));
    }

    // 根据 providerId 查询该医生/服务提供者的所有预约
    @GetMapping("/by-provider")
    public ResponseEntity<List<Appointment>> getByProvider(@RequestParam Long providerId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByProvider(providerId));
    }

    @PutMapping
    public ResponseEntity<Appointment> update(@RequestBody Appointment appointment) {
        return ResponseEntity.ok(appointmentService.updateAppointment(appointment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Appointment> cancel(
            @PathVariable Long id,
            @RequestBody CancelAppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id, request));
    }
}
