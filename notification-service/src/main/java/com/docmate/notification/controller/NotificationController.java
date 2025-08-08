package com.docmate.notification.controller;

import com.docmate.common.dto.response.ApiResponse;
import com.docmate.common.dto.response.PageResponse;
import com.docmate.common.entity.User;
import com.docmate.notification.dto.CreateNotificationRequest;
import com.docmate.notification.dto.NotificationDto;
import com.docmate.notification.service.NotificationService;
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
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "Notification management APIs")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @PostMapping
    @Operation(summary = "Create notification", description = "Create and send notification (Admin/System only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> createNotification(@Valid @RequestBody CreateNotificationRequest request) {
        notificationService.createAndSendNotification(request);
        return ResponseEntity.ok(ApiResponse.success("Notification sent successfully", "Notification created and sent"));
    }
    
    @GetMapping("/my")
    @Operation(summary = "Get user notifications", description = "Get current user's notifications")
    public ResponseEntity<ApiResponse<PageResponse<NotificationDto>>> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationDto> notifications = notificationService.getUserNotifications(currentUser.getId(), pageable);
        PageResponse<NotificationDto> pageResponse = PageResponse.of(
                notifications.getContent(), 
                notifications.getNumber(), 
                notifications.getSize(), 
                notifications.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }
    
    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications", description = "Get current user's unread notifications")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUnreadNotifications(@AuthenticationPrincipal User currentUser) {
        List<NotificationDto> notifications = notificationService.getUnreadNotifications(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }
    
    @GetMapping("/unread/count")
    @Operation(summary = "Get unread count", description = "Get count of unread notifications")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@AuthenticationPrincipal User currentUser) {
        long count = notificationService.getUnreadCount(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(count));
    }
    
    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Mark notification as read", description = "Mark a specific notification as read")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal User currentUser) {
        notificationService.markNotificationAsRead(notificationId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", "Notification updated"));
    }
    
    @PutMapping("/read-all")
    @Operation(summary = "Mark all as read", description = "Mark all notifications as read for current user")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(@AuthenticationPrincipal User currentUser) {
        notificationService.markAllAsRead(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", "Notifications updated"));
    }
    
    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete notification", description = "Delete a specific notification")
    public ResponseEntity<ApiResponse<String>> deleteNotification(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal User currentUser) {
        notificationService.deleteNotification(notificationId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Notification deleted successfully", "Notification removed"));
    }
    
    // Internal API endpoints for service-to-service communication
    @PostMapping("/internal/appointment-reminder")
    @Operation(summary = "Send appointment reminder", description = "Send appointment reminder (Internal API)")
    public ResponseEntity<ApiResponse<String>> sendAppointmentReminder(
            @RequestParam UUID appointmentId,
            @RequestParam UUID patientId,
            @RequestParam UUID doctorId,
            @RequestParam String appointmentTime) {
        // In a real implementation, you'd parse the appointmentTime properly
        notificationService.sendAppointmentReminder(appointmentId, patientId, doctorId, java.time.LocalDateTime.now());
        return ResponseEntity.ok(ApiResponse.success("Appointment reminder sent", "Reminders sent"));
    }
    
    @PostMapping("/internal/payment-notification")
    @Operation(summary = "Send payment notification", description = "Send payment status notification (Internal API)")
    public ResponseEntity<ApiResponse<String>> sendPaymentNotification(
            @RequestParam UUID userId,
            @RequestParam String paymentStatus,
            @RequestParam String appointmentDetails) {
        notificationService.sendPaymentNotification(userId, paymentStatus, appointmentDetails);
        return ResponseEntity.ok(ApiResponse.success("Payment notification sent", "Notification sent"));
    }
    
    @PostMapping("/internal/doctor-approval")
    @Operation(summary = "Send doctor approval notification", description = "Send doctor approval/rejection notification (Internal API)")
    public ResponseEntity<ApiResponse<String>> sendDoctorApprovalNotification(
            @RequestParam UUID doctorId,
            @RequestParam boolean approved) {
        notificationService.sendDoctorApprovalNotification(doctorId, approved);
        return ResponseEntity.ok(ApiResponse.success("Doctor approval notification sent", "Notification sent"));
    }
}
