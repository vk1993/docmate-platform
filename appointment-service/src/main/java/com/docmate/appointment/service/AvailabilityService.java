package com.docmate.appointment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilityService {

    // This would typically call the availability-service microservice
    // For now, implementing a simple availability check
    public boolean isDoctorAvailable(UUID doctorId, LocalDateTime appointmentDateTime, Integer durationMinutes) {
        if (doctorId == null || appointmentDateTime == null || durationMinutes == null) {
            return false;
        }
        
        // TODO: Implement actual availability check by calling availability-service
        // For now, assume doctors are available during business hours (9 AM - 5 PM)
        int hour = appointmentDateTime.getHour();
        boolean isBusinessHours = hour >= 9 && hour < 17;
        
        log.debug("Checking availability for doctor {} at {}: {}", doctorId, appointmentDateTime, isBusinessHours);
        return isBusinessHours;
    }
    
    public boolean isTimeSlotAvailable(UUID doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        // TODO: Implement actual time slot availability check
        return isDoctorAvailable(doctorId, startTime, 30);
    }
}
