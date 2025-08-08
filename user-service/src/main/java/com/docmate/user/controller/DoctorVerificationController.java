package com.docmate.user.controller;

import com.docmate.common.dto.DoctorVerificationDto;
import com.docmate.common.dto.SubmitVerificationRequest;
import com.docmate.user.service.DoctorVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/doctors/verification")
@RequiredArgsConstructor
@Validated
@Tag(name = "Doctor Verification", description = "Doctor verification document management")
public class DoctorVerificationController {

    private final DoctorVerificationService verificationService;

    @Operation(summary = "Submit verification documents", description = "Submit license and ID documents for doctor verification")
    @PostMapping("/submit")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorVerificationDto> submitVerificationDocuments(
            @Valid @RequestBody SubmitVerificationRequest request) {

        log.info("Received verification submission request for doctor: {}", request.getDoctorId());
        DoctorVerificationDto verification = verificationService.submitVerificationDocuments(request);
        return ResponseEntity.ok(verification);
    }

    @Operation(summary = "Get verification status", description = "Get current verification status for a doctor")
    @GetMapping("/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<DoctorVerificationDto> getVerificationStatus(@PathVariable UUID doctorId) {

        log.info("Fetching verification status for doctor: {}", doctorId);
        DoctorVerificationDto verification = verificationService.getDoctorVerification(doctorId);
        return ResponseEntity.ok(verification);
    }
}
