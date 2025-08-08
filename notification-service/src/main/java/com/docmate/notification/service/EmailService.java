package com.docmate.notification.service;

import com.docmate.common.enums.NotificationType;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {
    
    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;
    
    @Value("${app.email.from:noreply@docmate.com}")
    private String fromEmail;
    
    @Value("${app.email.from-name:DocMate}")
    private String fromName;
    
    public void sendNotificationEmail(String toEmail, String subject, String message, NotificationType type) {
        log.info("Sending email notification to: {} with subject: {}", toEmail, subject);
        
        try {
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(toEmail);
            Content content = new Content("text/html", buildEmailContent(subject, message, type));
            
            Mail mail = new Mail(from, subject, to, content);
            
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("Email sent successfully to: {}", toEmail);
            } else {
                log.error("Failed to send email. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
            }
            
        } catch (Exception e) {
            log.error("Error sending email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    public void sendAppointmentConfirmationEmail(String toEmail, String patientName, String doctorName, String appointmentTime) {
        String subject = "Appointment Confirmation - DocMate";
        String message = String.format(
                "Dear %s,\\n\\nYour appointment with Dr. %s has been confirmed for %s.\\n\\nThank you for choosing DocMate!",
                patientName, doctorName, appointmentTime
        );
        
        sendNotificationEmail(toEmail, subject, message, NotificationType.APPOINTMENT_SCHEDULED);
    }
    
    public void sendWelcomeEmail(String toEmail, String userName, String userRole) {
        String subject = "Welcome to DocMate!";
        String message = String.format(
                "Dear %s,\\n\\nWelcome to DocMate! Your %s account has been created successfully.\\n\\nGet started by completing your profile.",
                userName, userRole.toLowerCase()
        );
        
        sendNotificationEmail(toEmail, subject, message, NotificationType.SYSTEM_ALERT);
    }
    
    private String buildEmailContent(String subject, String message, NotificationType type) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>%s</title>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #007bff; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f9f9f9; }
                        .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                        .btn { display: inline-block; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>DocMate</h1>
                        </div>
                        <div class="content">
                            <h2>%s</h2>
                            <p>%s</p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2024 DocMate. All rights reserved.</p>
                            <p>This is an automated message. Please do not reply to this email.</p>
                        </div>
                    </div>
                </body>
                </html>
                """, subject, subject, message.replace("\\n", "<br>"));
    }
}
