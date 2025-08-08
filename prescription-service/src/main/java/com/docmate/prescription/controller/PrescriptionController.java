package com.docmate.prescription.controller;

import com.docmate.common.dto.response.ApiResponse;
import com.docmate.common.entity.User;
import com.docmate.prescription.dto.CreatePrescriptionRequest;
import com.docmate.prescription.dto.PrescriptionDto;
import com.docmate.prescription.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
@Tag(name = "Prescription Management", description = "Medical prescription management APIs")
@SecurityRequirement(name = "bearerAuth")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    @Operation(summary = "Create prescription", description = "Create new prescription (Doctor only)")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<PrescriptionDto>> createPrescription(
            @Valid @RequestBody CreatePrescriptionRequest request,
            @AuthenticationPrincipal User currentUser) {
        PrescriptionDto prescription = prescriptionService.createPrescription(request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Prescription created successfully", prescription));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get patient prescriptions", description = "Get all prescriptions for a patient")
    public ResponseEntity<ApiResponse<List<PrescriptionDto>>> getPatientPrescriptions(@PathVariable UUID patientId) {
        List<PrescriptionDto> prescriptions = prescriptionService.getPatientPrescriptions(patientId);
        return ResponseEntity.ok(ApiResponse.success(prescriptions));
    }

    @GetMapping("/{prescriptionId}")
    @Operation(summary = "Get prescription details", description = "Get prescription by ID")
    public ResponseEntity<ApiResponse<PrescriptionDto>> getPrescription(@PathVariable UUID prescriptionId) {
        PrescriptionDto prescription = prescriptionService.getPrescription(prescriptionId);
        return ResponseEntity.ok(ApiResponse.success(prescription));
    }
}
