package com.docmate.user.controller;

import com.docmate.common.dto.response.ApiResponse;
import com.docmate.common.dto.response.PageResponse;
import com.docmate.common.entity.User;
import com.docmate.common.dto.PatientDto;
import com.docmate.common.dto.PatientMedicalHistoryResponse;
import com.docmate.common.dto.PatientReportResponse;
import com.docmate.common.dto.UpdatePatientMedicalHistoryRequest;
import com.docmate.common.dto.PatientProfileDto;
import com.docmate.common.dto.UpdatePatientProfileDto;
import com.docmate.user.service.PatientService;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Management", description = "Patient profile management APIs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('PATIENT')")
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/me/profile")
    @Operation(summary = "Get current patient profile", description = "Get the current patient's profile")
    public ResponseEntity<PatientProfileDto> getPatientProfile(@AuthenticationPrincipal User currentUser) {
        PatientProfileDto profile = patientService.getPatientByEmail(currentUser.getEmail());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me/profile")
    @Operation(summary = "Update patient profile", description = "Update the current patient's profile")
    public ResponseEntity<PatientProfileDto> updatePatientProfile(
            @Valid @RequestBody UpdatePatientProfileDto request,
            @AuthenticationPrincipal User currentUser) {
        PatientProfileDto currentProfile = patientService.getPatientByEmail(currentUser.getEmail());
        PatientProfileDto updatedProfile = patientService.updatePatientProfile(currentProfile.id(), request);
        return ResponseEntity.ok(updatedProfile);
    }

    @PutMapping("/me/medical-history")
    @Operation(summary = "Update medical history", description = "Update patient's medical history")
    public ResponseEntity<PatientMedicalHistoryResponse> updateMedicalHistory(
            @Valid @RequestBody UpdatePatientMedicalHistoryRequest request,
            @AuthenticationPrincipal User currentUser) {
        PatientMedicalHistoryResponse response = patientService.updateMedicalHistory(currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/reports")
    @Operation(summary = "Get patient reports", description = "Get patient's medical reports")
    public ResponseEntity<PageResponse<PatientReportResponse>> getPatientReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PatientReportResponse> reports = patientService.getPatientReports(currentUser.getId(), pageable);
        return ResponseEntity.ok(PageResponse.of(reports));
    }

    @PostMapping("/me/reports")
    @Operation(summary = "Upload patient report", description = "Upload a new medical report")
    public ResponseEntity<PatientReportResponse> uploadPatientReport(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "testDate", required = false) LocalDate testDate,
            @AuthenticationPrincipal User currentUser) {

        PatientReportResponse response = patientService.uploadPatientReport(
            currentUser.getId(), file, title, description, testDate);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me/reports/{reportId}")
    @Operation(summary = "Delete patient report", description = "Delete a patient's medical report")
    public ResponseEntity<Void> deletePatientReport(
            @PathVariable UUID reportId,
            @AuthenticationPrincipal User currentUser) {
        patientService.deletePatientReport(currentUser.getId(), reportId);
        return ResponseEntity.noContent().build();
    }

    // Admin-only endpoints
    @PostMapping("/profile")
    @Operation(summary = "Create patient profile", description = "Create a new patient profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PatientDto>> createProfile(
            @Valid @RequestBody PatientDto patientDto,
            @AuthenticationPrincipal User currentUser) {
        PatientDto createdProfile = patientService.createPatientProfile(patientDto, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Patient profile created successfully", createdProfile));
    }

    @GetMapping("/{patientId}")
    @Operation(summary = "Get patient by ID", description = "Get patient profile by ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<PatientProfileDto>> getPatientById(@PathVariable UUID patientId) {
        PatientProfileDto patient = patientService.getPatientProfile(patientId);
        return ResponseEntity.ok(ApiResponse.success(patient));
    }

    @GetMapping
    @Operation(summary = "Get all patients", description = "Get all patients with pagination (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<PatientDto>>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PatientDto> patients = patientService.getAllPatients(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(patients)));
    }

    @DeleteMapping("/{patientId}")
    @Operation(summary = "Delete patient", description = "Delete patient profile (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deletePatient(@PathVariable UUID patientId) {
        patientService.deletePatient(patientId);
        return ResponseEntity.ok(ApiResponse.success("Patient deleted successfully", "Patient profile has been deactivated"));
    }
}
