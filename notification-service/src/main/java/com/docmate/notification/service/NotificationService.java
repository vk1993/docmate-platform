package com.docmate.notification.service;

import com.docmate.common.entity.Notification;
import com.docmate.common.entity.User;
import com.docmate.common.enums.NotificationType;
import com.docmate.common.exception.BusinessException;
import com.docmate.notification.dto.CreateNotificationRequest;
import com.docmate.notification.dto.NotificationDto;
import com.docmate.notification.mapper.NotificationMapper;
import com.docmate.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final EmailService emailService;
    private final SmsService smsService;
    private final UserService userService;
    
    @Async
    public void createAndSendNotification(CreateNotificationRequest request) {
        log.info("Creating notification for user: {} with type: {}", request.getUserId(), request.getType());
        
        // Get user details
        User user = userService.findById(request.getUserId());
        
        // Create in-app notification
        Notification notification = Notification.builder()
                .user(user)
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .isRead(false)
                .data(request.getData())
                .build();
        
        notification = notificationRepository.save(notification);
        
        // Send email notification if requested
        if (request.isSendEmail() && user.getEmail() != null) {
            try {
                emailService.sendNotificationEmail(user.getEmail(), request.getTitle(), request.getMessage(), request.getType());
                log.info("Email notification sent to: {}", user.getEmail());
            } catch (Exception e) {
                log.error("Failed to send email notification to {}: {}", user.getEmail(), e.getMessage());
            }
        }
        
        // Send SMS notification if requested
        if (request.isSendSms() && user.getPhone() != null) {
            try {
                smsService.sendNotificationSms(user.getPhone(), request.getMessage());
                log.info("SMS notification sent to: {}", user.getPhone());
            } catch (Exception e) {
                log.error("Failed to send SMS notification to {}: {}", user.getPhone(), e.getMessage());
            }
        }
        
        log.info("Notification created successfully with ID: {}", notification.getId());
    }
    
    public void sendAppointmentReminder(UUID appointmentId, UUID patientId, UUID doctorId, LocalDateTime appointmentTime) {
        log.info("Sending appointment reminder for appointment: {}", appointmentId);
        
        String message = String.format("Reminder: You have an appointment scheduled for %s", appointmentTime);
        
        // Send reminder to patient
        CreateNotificationRequest patientNotification = CreateNotificationRequest.builder()
                .userId(patientId)
                .title("Appointment Reminder")
                .message(message)
                .type(NotificationType.APPOINTMENT_REMINDER)
                .sendEmail(true)
                .sendSms(true)
                .build();
        
        createAndSendNotification(patientNotification);
        
        // Send reminder to doctor
        CreateNotificationRequest doctorNotification = CreateNotificationRequest.builder()
                .userId(doctorId)
                .title("Upcoming Appointment")
                .message("You have an upcoming appointment with a patient at " + appointmentTime)
                .type(NotificationType.APPOINTMENT_REMINDER)
                .sendEmail(true)
                .build();
        
        createAndSendNotification(doctorNotification);
    }
    
    public void sendPaymentNotification(UUID userId, String paymentStatus, String appointmentDetails) {
        log.info("Sending payment notification to user: {} with status: {}", userId, paymentStatus);
        
        String title = paymentStatus.equals("COMPLETED") ? "Payment Successful" : "Payment Failed";
        String message = String.format("Your payment for %s has been %s", appointmentDetails, paymentStatus.toLowerCase());
        
        CreateNotificationRequest notification = CreateNotificationRequest.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(paymentStatus.equals("COMPLETED") ? NotificationType.PAYMENT_RECEIVED : NotificationType.PAYMENT_FAILED)
                .sendEmail(true)
                .build();
        
        createAndSendNotification(notification);
    }
    
    public void sendDoctorApprovalNotification(UUID doctorId, boolean approved) {
        log.info("Sending doctor approval notification to: {} - approved: {}", doctorId, approved);
        
        String title = approved ? "Doctor Profile Approved" : "Doctor Profile Rejected";
        String message = approved ? 
                "Congratulations! Your doctor profile has been approved. You can now start accepting appointments." :
                "Your doctor profile has been rejected. Please contact support for more information.";
        
        CreateNotificationRequest notification = CreateNotificationRequest.builder()
                .userId(doctorId)
                .title(title)
                .message(message)
                .type(approved ? NotificationType.DOCTOR_APPROVED : NotificationType.DOCTOR_REJECTED)
                .sendEmail(true)
                .build();
        
        createAndSendNotification(notification);
    }
    
    @Transactional(readOnly = true)
    public Page<NotificationDto> getUserNotifications(UUID userId, Pageable pageable) {
        log.info("Fetching notifications for user: {}", userId);
        
        Page<Notification> notifications = notificationRepository.findByUserId(userId, pageable);
        return notifications.map(notificationMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(UUID userId) {
        log.info("Fetching unread notifications for user: {}", userId);
        
        List<Notification> notifications = notificationRepository.findUnreadByUserId(userId);
        return notifications.stream()
                .map(notificationMapper::toDto)
                .toList();
    }
    
    public void markNotificationAsRead(UUID notificationId, UUID userId) {
        log.info("Marking notification {} as read for user: {}", notificationId, userId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException("NOTIFICATION_NOT_FOUND", "Notification not found", 404));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED", "Cannot mark notification as read", 403);
        }
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
    
    public void markAllAsRead(UUID userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        
        notificationRepository.markAllAsReadByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }
    
    public void deleteNotification(UUID notificationId, UUID userId) {
        log.info("Deleting notification {} for user: {}", notificationId, userId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException("NOTIFICATION_NOT_FOUND", "Notification not found", 404));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED", "Cannot delete notification", 403);
        }
        
        notificationRepository.delete(notification);
    }
}
