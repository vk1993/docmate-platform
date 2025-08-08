package com.docmate.prescription.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrescriptionDto {
    
    private UUID id;
    private UUID appointmentId;
    private UUID doctorId;
    private UUID patientId;
    private String diagnosis;
    private String symptoms;
    private String advice;
    private List<MedicineDto> medicines;
    private LocalDateTime createdDate;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicineDto {
        private UUID id;
        private String name;
        private String dosage;
        private String frequency;
        private String duration;
        private String instructions;
    }
}
