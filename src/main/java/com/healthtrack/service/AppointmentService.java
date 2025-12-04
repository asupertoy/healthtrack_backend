package com.healthtrack.service;

import com.healthtrack.dto.CancelAppointmentRequest;
import com.healthtrack.dto.CreateAppointmentRequest;
import com.healthtrack.entity.Appointment;
import com.healthtrack.entity.Provider;
import com.healthtrack.entity.User;
import com.healthtrack.repository.AppointmentRepository;
import com.healthtrack.repository.ProviderRepository;
import com.healthtrack.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              UserRepository userRepository,
                              ProviderRepository providerRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;
    }

    // 供内部使用的直接保存实体的方法
    public Appointment createAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    // 前端友好的创建预约方法，根据 bookingUserId / providerId 构建实体
    public Appointment createAppointment(CreateAppointmentRequest request) {
        User bookingUser = userRepository.findById(request.getBookingUserId())
                .orElseThrow(() -> new RuntimeException("Booking user not found"));
        Provider provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        Appointment appointment = new Appointment();
        appointment.setBookingUser(bookingUser);
        appointment.setProvider(provider);

        LocalDateTime scheduledAt = request.getScheduledAt();
        if (scheduledAt == null) {
            scheduledAt = LocalDateTime.now(); // 不传时，默认当前时间
        }
        appointment.setScheduledAt(scheduledAt);

        appointment.setConsultationType(request.getConsultationType());
        appointment.setMemo(request.getMemo());
        // 初始状态为 scheduled（实体中默认已经是 scheduled）

        return appointmentRepository.save(appointment);
    }

    public Optional<Appointment> getAppointment(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment updateAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    public List<Appointment> getAppointmentsByBookingUser(Long bookingUserId) {
        return appointmentRepository.findByBookingUser_UserId(bookingUserId);
    }

    public List<Appointment> getAppointmentsByProvider(Long providerId) {
        return appointmentRepository.findByProvider_ProviderId(providerId);
    }

    // 综合搜索：支持可选的 bookingUserId / providerId / 时间范围过滤
    public List<Appointment> searchAppointments(Long bookingUserId,
                                                Long providerId,
                                                java.time.LocalDateTime startDate,
                                                java.time.LocalDateTime endDate) {
        return appointmentRepository.searchAppointments(bookingUserId, providerId, startDate, endDate);
    }

    public Appointment cancelAppointment(Long id, CancelAppointmentRequest request) {
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        String reason = request.getCancellationReason();
        if (reason == null || reason.trim().isEmpty()) {
            reason = "Cancelled without specified reason";
        }

        appt.setStatus(Appointment.AppointmentStatus.cancelled);
        appt.setCancelledAt(request.getCancelledAt() != null ? request.getCancelledAt() : LocalDateTime.now());
        appt.setCancellationReason(reason);

        return appointmentRepository.save(appt);
    }
}
