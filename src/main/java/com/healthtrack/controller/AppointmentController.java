package com.healthtrack.controller;

import com.healthtrack.dto.CancelAppointmentRequest;
import com.healthtrack.dto.CreateAppointmentRequest;
import com.healthtrack.entity.Appointment;
import com.healthtrack.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
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
