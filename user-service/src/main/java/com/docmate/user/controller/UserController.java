package com.docmate.user.controller;

import com.docmate.common.dto.UserDto;
import com.docmate.common.dto.response.ApiResponse;
import com.docmate.common.enums.ConsultationMode;
import com.docmate.user.service.JwtService;
import com.docmate.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "User profile and management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    // Patient endpoints
    @GetMapping("/patients/me")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get current patient profile", description = "Get the authenticated patient's profile")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentPatientProfile(
            @RequestHeader("Authorization") String authHeader) {
        
        String email = extractEmailFromToken(authHeader);
        UserDto profile = userService.getUserByEmail(email);
        
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/patients/me")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Update current patient profile", description = "Update the authenticated patient's profile")
    public ResponseEntity<ApiResponse<UserDto>> updateCurrentPatientProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserDto updateRequest) {
        
        UUID uuid = extractUidFromToken(authHeader);
        UserDto updatedProfile = userService.updateUser(uuid, updateRequest);
        
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedProfile));
    }

    @PostMapping("/patients/me/change-password")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Change patient password", description = "Change the authenticated patient's password")
    public ResponseEntity<ApiResponse<String>> changePatientPassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        
        String email = extractEmailFromToken(authHeader);
        userService.changePassword(email, currentPassword, newPassword);
        
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    @GetMapping("/patients/{patientId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Get patient by ID", description = "Get patient profile by ID (doctors and admins only)")
    public ResponseEntity<ApiResponse<UserDto>> getPatientById(@PathVariable UUID patientId) {
        UserDto patient = userService.getUserById(patientId);
        return ResponseEntity.ok(ApiResponse.success(patient));
    }

    // Doctor endpoints
    @GetMapping("/doctors/me")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Get current doctor profile", description = "Get the authenticated doctor's profile")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentDoctorProfile(
            @RequestHeader("Authorization") String authHeader) {
        
        String email = extractEmailFromToken(authHeader);
        UserDto profile = userService.getUserProfile(email);
        
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/doctors/me")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Update current doctor profile", description = "Update the authenticated doctor's profile")
    public ResponseEntity<ApiResponse<UserDto>> updateCurrentDoctorProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserDto updateRequest) {
        
        String email = extractEmailFromToken(authHeader);
        UserDto updatedProfile = userService.updateDoctorProfile(email, updateRequest);
        
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedProfile));
    }

    @PostMapping("/doctors/me/change-password")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Change doctor password", description = "Change the authenticated doctor's password")
    public ResponseEntity<ApiResponse<String>> changeDoctorPassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        
        String email = extractEmailFromToken(authHeader);
        userService.changePassword(email, currentPassword, newPassword);
        
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    @GetMapping("/doctors")
    @Operation(summary = "Get all doctors", description = "Get paginated list of verified doctors")
    public ResponseEntity<ApiResponse<Page<UserDto>>> getAllDoctors(
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<UserDto> doctors = userService.getAllDoctors(pageable);
        return ResponseEntity.ok(ApiResponse.success(doctors));
    }

    @GetMapping("/doctors/search")
    @Operation(summary = "Search doctors with filters", description = "Search doctors by query, specialization, consultation mode, and fee")
    public ResponseEntity<ApiResponse<Page<UserDto>>> searchDoctorsWithFilters(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) BigDecimal maxFee,
            @RequestParam(required = false) ConsultationMode consultationType,
            @RequestParam(required = false) Boolean emergencyAvailable,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<UserDto> doctors = userService.searchDoctorsWithFilters(
            query, specialization, condition, maxFee, consultationType, emergencyAvailable, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(doctors));
    }

    // Statistics endpoints
    @GetMapping("/users/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user statistics", description = "Get user statistics (admins only)")
    public ResponseEntity<ApiResponse<UserStatsDto>> getUserStats() {
        UserStatsDto stats = UserStatsDto.builder()
                .totalUsers(userService.getTotalActiveUsers())
                .totalPatients(userService.getTotalActivePatients())
                .totalDoctors(userService.getTotalActiveDoctors())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if user service is running")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("User service is healthy"));
    }

    // Helper method to extract email from JWT token
    private String extractEmailFromToken(String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        return jwtService.extractUsername(token);
    }

    // Helper method to extract email from JWT token
    private UUID extractUidFromToken(String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        return jwtService.getUserIdFromToken(token);
    }

    // Inner DTO class for statistics
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UserStatsDto {
        private long totalUsers;
        private long totalPatients;
        private long totalDoctors;
    }
}
