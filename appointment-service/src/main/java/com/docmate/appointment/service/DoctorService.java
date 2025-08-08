package com.docmate.appointment.service;

import com.docmate.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    // This would typically call the user-service or doctor microservice
    // For now, implementing a simple validation
    public void validateDoctorExists(UUID doctorId) {
        if (doctorId == null) {
            throw new BusinessException("INVALID_DOCTOR", "Doctor ID cannot be null", 400);
        }
        
        // TODO: Implement actual validation by calling user-service or doctor microservice
        // For now, assume all non-null doctor IDs are valid
        log.debug("Validating doctor exists: {}", doctorId);
    }
    
    public boolean isDoctorActive(UUID doctorId) {
        // TODO: Implement actual check by calling user-service
        return true;
    }
}
