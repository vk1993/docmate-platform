package com.docmate.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Internal controller for sending email notifications. In a real implementation
 * this service would render templates and send emails via Spring Mail and
 * MailHog (in development). For this skeleton the endpoint accepts the
 * request and returns HTTP 200.
 */
@RestController
@RequestMapping("/internal/notify")
@Validated
public class EmailNotificationController {

    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(@RequestBody Map<String, Object> payload) {
        // In a full implementation, you would use the payload to render and send an email.
        return ResponseEntity.ok().build();
    }
}