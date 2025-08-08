package com.docmate.admin.controller;

import com.docmate.admin.dto.DashboardStatsDto;
import com.docmate.admin.service.AdminDashboardService;
import com.docmate.common.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard", description = "Admin dashboard and system management APIs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard/stats")
    @Operation(summary = "Get dashboard statistics", description = "Get comprehensive dashboard statistics")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats() {
        DashboardStatsDto stats = adminDashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/dashboard/revenue")
    @Operation(summary = "Get revenue statistics", description = "Get revenue statistics for a date range")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Map<String, Object> revenueStats = adminDashboardService.getRevenueStats(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(revenueStats));
    }

    @GetMapping("/dashboard/appointments")
    @Operation(summary = "Get appointment statistics", description = "Get appointment statistics and trends")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAppointmentStats(
            @RequestParam(defaultValue = "30") int days) {
        Map<String, Object> appointmentStats = adminDashboardService.getAppointmentStats(days);
        return ResponseEntity.ok(ApiResponse.success(appointmentStats));
    }

    @GetMapping("/dashboard/users")
    @Operation(summary = "Get user statistics", description = "Get user registration and activity statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats() {
        Map<String, Object> userStats = adminDashboardService.getUserStats();
        return ResponseEntity.ok(ApiResponse.success(userStats));
    }

    @GetMapping("/system/health")
    @Operation(summary = "Get system health", description = "Get system health and service status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemHealth() {
        Map<String, Object> healthStats = adminDashboardService.getSystemHealth();
        return ResponseEntity.ok(ApiResponse.success(healthStats));
    }

    @PostMapping("/system/maintenance")
    @Operation(summary = "Enable maintenance mode", description = "Enable or disable system maintenance mode")
    public ResponseEntity<ApiResponse<String>> toggleMaintenanceMode(@RequestParam boolean enabled) {
        adminDashboardService.toggleMaintenanceMode(enabled);
        String message = enabled ? "Maintenance mode enabled" : "Maintenance mode disabled";
        return ResponseEntity.ok(ApiResponse.success(message, "System maintenance mode updated"));
    }
}
