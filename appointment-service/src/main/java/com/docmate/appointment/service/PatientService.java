package com.docmate.appointment.service;

import com.docmate.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    // This would typically call the user-service or patient microservice
    // For now, implementing a simple validation
    public void validatePatientExists(UUID patientId) {
        if (patientId == null) {
            throw new BusinessException("INVALID_PATIENT", "Patient ID cannot be null", 400);
        }
        
        // TODO: Implement actual validation by calling user-service or patient microservice
        // For now, assume all non-null patient IDs are valid
        log.debug("Validating patient exists: {}", patientId);
    }
    
    public boolean isPatientActive(UUID patientId) {
        // TODO: Implement actual check by calling user-service
        return true;
    }
}
