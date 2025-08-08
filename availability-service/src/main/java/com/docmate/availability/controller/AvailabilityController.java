package com.docmate.availability.controller;

import com.docmate.availability.service.AvailabilityService;
import com.docmate.common.dto.AvailabilityDto;
import com.docmate.common.dto.CreateAvailabilityRequest;
import com.docmate.common.dto.response.ApiResponse;
import com.docmate.common.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
@Tag(name = "Doctor Availability", description = "Doctor availability management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping("/recurring")
    @Operation(summary = "Set recurring availability", description = "Set doctor's recurring weekly availability")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<AvailabilityDto>> setRecurringAvailability(
            @Valid @RequestBody CreateAvailabilityRequest request,
            @AuthenticationPrincipal User currentUser) {
        AvailabilityDto availability = availabilityService.setRecurringAvailability(request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Recurring availability set successfully", availability));
    }

    @PostMapping("/adhoc")
    @Operation(summary = "Set adhoc availability", description = "Set doctor's one-time availability slot")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<AvailabilityDto>> setAdhocAvailability(
            @Valid @RequestBody CreateAvailabilityRequest request,
            @AuthenticationPrincipal User currentUser) {
        AvailabilityDto availability = availabilityService.setAdhocAvailability(request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Adhoc availability set successfully", availability));
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get doctor availability", description = "Get doctor's availability for a date range")
    public ResponseEntity<ApiResponse<List<AvailabilityDto>>> getDoctorAvailability(
            @PathVariable UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AvailabilityDto> availability = availabilityService.getDoctorAvailability(doctorId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(availability));
    }

    @GetMapping("/slots/{doctorId}")
    @Operation(summary = "Get available time slots", description = "Get available appointment slots for a doctor on a specific date")
    public ResponseEntity<ApiResponse<List<AvailabilityDto>>> getAvailableSlots(
            @PathVariable UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AvailabilityDto> slots = availabilityService.getAvailableSlots(doctorId, date);
        return ResponseEntity.ok(ApiResponse.success(slots));
    }

    @DeleteMapping("/{availabilityId}")
    @Operation(summary = "Delete availability", description = "Delete doctor's availability slot")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<String>> deleteAvailability(
            @PathVariable UUID availabilityId,
            @AuthenticationPrincipal User currentUser) {
        availabilityService.deleteAvailability(availabilityId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Availability deleted successfully", "Slot removed"));
    }
}
