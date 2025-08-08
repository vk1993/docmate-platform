package com.docmate.admin.controller;

import com.docmate.admin.service.AdminVerificationService;
import com.docmate.common.dto.DoctorVerificationDto;
import com.docmate.common.dto.VerificationReviewRequest;
import com.docmate.common.enums.VerificationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/admin/verifications")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admin - Doctor Verification", description = "Admin endpoints for managing doctor verifications")
@PreAuthorize("hasRole('ADMIN')")
public class AdminVerificationController {

    private final AdminVerificationService adminVerificationService;

    @Operation(summary = "Get pending verifications", description = "Get all pending doctor verifications for review")
    @GetMapping("/pending")
    public ResponseEntity<List<DoctorVerificationDto>> getPendingVerifications() {
        log.info("Admin fetching pending verifications");
        List<DoctorVerificationDto> verifications = adminVerificationService.getPendingVerifications();
        return ResponseEntity.ok(verifications);
    }

    @Operation(summary = "Get verification details", description = "Get detailed information about a specific verification")
    @GetMapping("/{verificationId}")
    public ResponseEntity<DoctorVerificationDto> getVerificationDetails(@PathVariable UUID verificationId) {
        log.info("Admin fetching verification details: {}", verificationId);
        DoctorVerificationDto verification = adminVerificationService.getVerificationById(verificationId);
        return ResponseEntity.ok(verification);
    }

    @Operation(summary = "Review verification", description = "Approve or reject a doctor verification")
    @PostMapping("/review")
    public ResponseEntity<DoctorVerificationDto> reviewVerification(
            @Valid @RequestBody VerificationReviewRequest request,
            Authentication authentication) {

        UUID adminUserId = UUID.fromString(authentication.getName());
        log.info("Admin {} reviewing verification: {}", adminUserId, request.getVerificationId());

        DoctorVerificationDto verification = adminVerificationService.reviewVerification(request, adminUserId);
        return ResponseEntity.ok(verification);
    }

    @Operation(summary = "Get verifications by status", description = "Get all verifications filtered by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<DoctorVerificationDto>> getVerificationsByStatus(@PathVariable VerificationStatus status) {
        log.info("Admin fetching verifications with status: {}", status);
        List<DoctorVerificationDto> verifications = adminVerificationService.getVerificationsByStatus(status);
        return ResponseEntity.ok(verifications);
    }

    @Operation(summary = "Get verification statistics", description = "Get count of pending verifications")
    @GetMapping("/stats/pending-count")
    public ResponseEntity<Long> getPendingVerificationCount() {
        log.info("Admin fetching pending verification count");
        long count = adminVerificationService.getPendingVerificationCount();
        return ResponseEntity.ok(count);
    }
}
