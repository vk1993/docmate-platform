package com.docmate.user.controller;

import com.docmate.common.dto.response.ApiResponse;
import com.docmate.common.dto.response.PageResponse;
import com.docmate.common.entity.User;
import com.docmate.common.dto.DoctorDto;
import com.docmate.common.dto.DoctorSearchResponse;
import com.docmate.common.dto.DoctorStatsResponse;
import com.docmate.common.dto.UpdateDoctorProfileRequest;
import com.docmate.user.service.DoctorService;
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

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Management", description = "Doctor profile management APIs")
@SecurityRequirement(name = "bearerAuth")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/search")
    @Operation(summary = "Search doctors", description = "Search doctors with filters")
    public ResponseEntity<PageResponse<DoctorSearchResponse>> searchDoctors(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) UUID specializationId,
            @RequestParam(required = false) UUID conditionId,
            @RequestParam(required = false) BigDecimal maxFee,
            @RequestParam(required = false) String consultationType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorSearchResponse> doctors = doctorService.searchDoctors(
                query, specializationId, conditionId, maxFee, consultationType, pageable);

        return ResponseEntity.ok(PageResponse.of(doctors));
    }

    @GetMapping("/me/profile")
    @Operation(summary = "Get current doctor profile", description = "Get the current doctor's profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorDto> getDoctorProfile(@AuthenticationPrincipal User currentUser) {
        DoctorDto profile = doctorService.getDoctorByEmail(currentUser.getEmail());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me/profile")
    @Operation(summary = "Update doctor profile", description = "Update the current doctor's profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorDto> updateDoctorProfile(
            @Valid @RequestBody UpdateDoctorProfileRequest request,
            @AuthenticationPrincipal User currentUser) {
        DoctorDto currentProfile = doctorService.getDoctorByEmail(currentUser.getEmail());
        DoctorDto updatedProfile = doctorService.updateDoctorProfile(currentProfile.getId(), request);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping("/me/stats")
    @Operation(summary = "Get doctor statistics", description = "Get current doctor's statistics")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorStatsResponse> getDoctorStats(@AuthenticationPrincipal User currentUser) {
        DoctorStatsResponse stats = doctorService.getDoctorStats(currentUser.getId());
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/profile")
    @Operation(summary = "Create doctor profile", description = "Create a new doctor profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorDto>> createProfile(
            @Valid @RequestBody DoctorDto doctorDto,
            @AuthenticationPrincipal User currentUser) {
        DoctorDto createdProfile = doctorService.createDoctorProfile(doctorDto, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Doctor profile created successfully - Pending approval", createdProfile));
    }

    @GetMapping("/{doctorId}")
    @Operation(summary = "Get doctor by ID", description = "Get doctor profile by ID")
    public ResponseEntity<ApiResponse<DoctorDto>> getDoctorById(@PathVariable UUID doctorId) {
        DoctorDto doctor = doctorService.getDoctorProfile(doctorId);
        return ResponseEntity.ok(ApiResponse.success(doctor));
    }

    @PutMapping("/{doctorId}")
    @Operation(summary = "Update doctor by ID", description = "Update doctor profile by ID (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DoctorDto>> updateDoctorById(
            @PathVariable UUID doctorId,
            @Valid @RequestBody UpdateDoctorProfileRequest request) {
        DoctorDto updatedProfile = doctorService.updateDoctorProfile(doctorId, request);
        return ResponseEntity.ok(ApiResponse.success("Doctor profile updated successfully", updatedProfile));
    }

    @PostMapping("/{doctorId}/approve")
    @Operation(summary = "Approve doctor", description = "Approve doctor profile (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DoctorDto>> approveDoctor(@PathVariable UUID doctorId) {
        DoctorDto approvedDoctor = doctorService.approveDoctorProfile(doctorId);
        return ResponseEntity.ok(ApiResponse.success("Doctor approved successfully", approvedDoctor));
    }

    @PostMapping("/{doctorId}/reject")
    @Operation(summary = "Reject doctor", description = "Reject doctor profile (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DoctorDto>> rejectDoctor(@PathVariable UUID doctorId) {
        DoctorDto rejectedDoctor = doctorService.rejectDoctorProfile(doctorId);
        return ResponseEntity.ok(ApiResponse.success("Doctor rejected successfully", rejectedDoctor));
    }

    @GetMapping("/approved")
    @Operation(summary = "Get approved doctors", description = "Get all approved doctors with pagination")
    public ResponseEntity<ApiResponse<PageResponse<DoctorDto>>> getApprovedDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorDto> doctors = doctorService.getApprovedDoctors(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(doctors)));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending approval doctors", description = "Get doctors pending approval (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<DoctorDto>>> getPendingApprovalDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorDto> doctors = doctorService.getPendingApprovalDoctors(pageable);
        PageResponse<DoctorDto> pageResponse = PageResponse.of(
                doctors.getContent(),
                doctors.getNumber(),
                doctors.getSize(),
                doctors.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @GetMapping("/specialization/{specializationId}")
    @Operation(summary = "Get doctors by specialization", description = "Get doctors by specialization ID")
    public ResponseEntity<ApiResponse<PageResponse<DoctorDto>>> getDoctorsBySpecialization(
            @PathVariable UUID specializationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorDto> doctors = doctorService.getDoctorsBySpecialization(specializationId, pageable);
        PageResponse<DoctorDto> pageResponse = PageResponse.of(
                doctors.getContent(),
                doctors.getNumber(),
                doctors.getSize(),
                doctors.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @DeleteMapping("/{doctorId}")
    @Operation(summary = "Delete doctor", description = "Delete doctor profile (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteDoctor(@PathVariable UUID doctorId) {
        doctorService.deleteDoctor(doctorId);
        return ResponseEntity.ok(ApiResponse.success("Doctor deleted successfully", "Doctor profile has been deactivated"));
    }

    @GetMapping("/count/approved")
    @Operation(summary = "Get approved doctor count", description = "Get total approved doctor count")
    public ResponseEntity<ApiResponse<Long>> getApprovedDoctorCount() {
        long count = doctorService.getApprovedDoctorCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/count/pending")
    @Operation(summary = "Get pending doctor count", description = "Get pending approval doctor count (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getPendingDoctorCount() {
        long count = doctorService.getPendingApprovalDoctorCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
