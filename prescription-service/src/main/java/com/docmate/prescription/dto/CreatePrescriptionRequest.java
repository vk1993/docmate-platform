package com.docmate.prescription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreatePrescriptionRequest {
    
    @NotNull(message = "Appointment ID is required")
    private UUID appointmentId;
    
    @NotNull(message = "Patient ID is required")
    private UUID patientId;
    
    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;
    
    private String symptoms;
    private String advice;
    
    private List<MedicineRequest> medicines;
    
    @Data
    public static class MedicineRequest {
        @NotBlank(message = "Medicine name is required")
        private String name;
        
        @NotBlank(message = "Dosage is required")
        private String dosage;
        
        @NotBlank(message = "Frequency is required")
        private String frequency;
        
        @NotBlank(message = "Duration is required")
        private String duration;
        
        private String instructions;
    }
}
