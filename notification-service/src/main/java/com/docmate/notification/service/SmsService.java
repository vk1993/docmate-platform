package com.docmate.notification.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Slf4j
@Service
public class SmsService {
    
    @Value("${twilio.account.sid}")
    private String accountSid;
    
    @Value("${twilio.auth.token}")
    private String authToken;
    
    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;
    
    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }
    
    public void sendNotificationSms(String toPhoneNumber, String message) {
        log.info("Sending SMS notification to: {}", toPhoneNumber);
        
        try {
            // Ensure phone number has country code
            String formattedPhoneNumber = formatPhoneNumber(toPhoneNumber);
            
            Message twilioMessage = Message.creator(
                    new PhoneNumber(formattedPhoneNumber),
                    new PhoneNumber(fromPhoneNumber),
                    message
            ).create();
            
            log.info("SMS sent successfully to: {} with SID: {}", toPhoneNumber, twilioMessage.getSid());
            
        } catch (Exception e) {
            log.error("Error sending SMS to {}: {}", toPhoneNumber, e.getMessage());
            throw new RuntimeException("Failed to send SMS", e);
        }
    }
    
    public void sendAppointmentReminderSms(String toPhoneNumber, String doctorName, String appointmentTime) {
        String message = String.format(
                "DocMate Reminder: Your appointment with Dr. %s is scheduled for %s. Please arrive 15 minutes early.",
                doctorName, appointmentTime
        );
        
        sendNotificationSms(toPhoneNumber, message);
    }
    
    public void sendAppointmentConfirmationSms(String toPhoneNumber, String doctorName, String appointmentTime) {
        String message = String.format(
                "DocMate: Your appointment with Dr. %s has been confirmed for %s. Reply CANCEL to cancel.",
                doctorName, appointmentTime
        );
        
        sendNotificationSms(toPhoneNumber, message);
    }
    
    public void sendPaymentConfirmationSms(String toPhoneNumber, String amount, String appointmentDetails) {
        String message = String.format(
                "DocMate: Payment of $%s for %s has been processed successfully. Thank you!",
                amount, appointmentDetails
        );
        
        sendNotificationSms(toPhoneNumber, message);
    }
    
    private String formatPhoneNumber(String phoneNumber) {
        // Remove any non-digit characters
        String digits = phoneNumber.replaceAll("[^\\d]", "");
        
        // Add country code if not present (assuming US +1)
        if (!digits.startsWith("1") && digits.length() == 10) {
            digits = "1" + digits;
        }
        
        return "+" + digits;
    }
}
