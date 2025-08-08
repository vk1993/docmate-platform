package com.docmate.availability.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Controller exposing doctor availability endpoints.
 */
@RestController
@RequestMapping("/api/doctors/me/availability")
@Validated
public class DoctorAvailabilityController {

    @GetMapping("/recurring")
    public List<String> getRecurring(Authentication authentication) {
        // Return an empty list for demonstration.
        return Collections.emptyList();
    }

    @PostMapping("/recurring")
    public ResponseEntity<Void> createRecurring(@RequestBody String slot, Authentication authentication) {
        // Accept slot definition and return 201 Created with no content.
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/recurring/{id}")
    public ResponseEntity<Void> deleteRecurring(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/adhoc")
    public List<String> getAdhoc(@RequestParam(required = false) LocalDateTime start,
                                 @RequestParam(required = false) LocalDateTime end,
                                 Authentication authentication) {
        return Collections.emptyList();
    }

    @PostMapping("/adhoc")
    public ResponseEntity<Void> createAdhoc(@RequestBody String slot, Authentication authentication) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/adhoc/{id}")
    public ResponseEntity<Void> deleteAdhoc(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.noContent().build();
    }
}