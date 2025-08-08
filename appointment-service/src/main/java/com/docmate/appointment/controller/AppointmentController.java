package com.docmate.appointment.controller;

import com.docmate.appointment.dto.AppointmentDto;
import com.docmate.appointment.dto.CreateAppointmentRequest;
import com.docmate.appointment.service.AppointmentService;
import com.docmate.common.dto.response.ApiResponse;
import com.docmate.common.dto.response.PageResponse;
import com.docmate.common.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment Management", description = "Appointment booking and management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @Operation(summary = "Book appointment", description = "Book a new appointment with a doctor")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<AppointmentDto>> bookAppointment(
            @Valid @RequestBody CreateAppointmentRequest request,
            @AuthenticationPrincipal User currentUser) {
        // Set the patient ID from the authenticated user
        request.setPatientId(currentUser.getId());
        AppointmentDto appointment = appointmentService.createAppointment(request);
        return ResponseEntity.ok(ApiResponse.success("Appointment booked successfully", appointment));
    }

    @GetMapping("/{appointmentId}")
    @Operation(summary = "Get appointment details", description = "Get appointment details by ID")
    public ResponseEntity<ApiResponse<AppointmentDto>> getAppointment(@PathVariable UUID appointmentId) {
        AppointmentDto appointment = appointmentService.getAppointment(appointmentId);
        return ResponseEntity.ok(ApiResponse.success(appointment));
    }

    @GetMapping("/patient/my")
    @Operation(summary = "Get patient appointments", description = "Get current patient's appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<PageResponse<AppointmentDto>>> getMyAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AppointmentDto> appointments = appointmentService.getPatientAppointments(currentUser.getId(), pageable);
        PageResponse<AppointmentDto> pageResponse = PageResponse.of(
                appointments.getContent(),
                appointments.getNumber(),
                appointments.getSize(),
                appointments.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @GetMapping("/doctor/my")
    @Operation(summary = "Get doctor appointments", description = "Get current doctor's appointments")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<PageResponse<AppointmentDto>>> getMyDoctorAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AppointmentDto> appointments = appointmentService.getDoctorAppointments(currentUser.getId(), pageable);
        PageResponse<AppointmentDto> pageResponse = PageResponse.of(
                appointments.getContent(),
                appointments.getNumber(),
                appointments.getSize(),
                appointments.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming appointments", description = "Get upcoming appointments for current user")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getUpcomingAppointments(
            @AuthenticationPrincipal User currentUser) {
        boolean isDoctor = currentUser.getRole().name().equals("DOCTOR");
        List<AppointmentDto> appointments = appointmentService.getUpcomingAppointments(currentUser.getId(), isDoctor);
        return ResponseEntity.ok(ApiResponse.success(appointments));
    }

    @PutMapping("/{appointmentId}/confirm")
    @Operation(summary = "Confirm appointment", description = "Confirm appointment (Doctor only)")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentDto>> confirmAppointment(@PathVariable UUID appointmentId) {
        AppointmentDto appointment = appointmentService.confirmAppointment(appointmentId);
        return ResponseEntity.ok(ApiResponse.success("Appointment confirmed successfully", appointment));
    }

    @PutMapping("/{appointmentId}/cancel")
    @Operation(summary = "Cancel appointment", description = "Cancel appointment")
    public ResponseEntity<ApiResponse<AppointmentDto>> cancelAppointment(
            @PathVariable UUID appointmentId,
            @RequestParam String reason,
            @AuthenticationPrincipal User currentUser) {
        AppointmentDto appointment = appointmentService.cancelAppointment(appointmentId, reason, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Appointment cancelled successfully", appointment));
    }

    @PutMapping("/{appointmentId}/complete")
    @Operation(summary = "Complete appointment", description = "Mark appointment as completed (Doctor only)")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentDto>> completeAppointment(
            @PathVariable UUID appointmentId,
            @RequestParam(required = false) String notes) {
        AppointmentDto appointment = appointmentService.completeAppointment(appointmentId, notes);
        return ResponseEntity.ok(ApiResponse.success("Appointment completed successfully", appointment));
    }
}
