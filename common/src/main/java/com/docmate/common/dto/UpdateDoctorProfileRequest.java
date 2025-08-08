package com.docmate.common.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDoctorProfileRequest {

    @Size(max = 50, message = "License number must not exceed 50 characters")
    private String licenseNumber;

    private UUID specializationId;
    private Integer experienceYears;
    private BigDecimal feePerConsultation;

    @Size(max = 2000, message = "Bio must not exceed 2000 characters")
    private String bio;

    @Size(max = 200, message = "Clinic name must not exceed 200 characters")
    private String clinicName;

    private Boolean videoConsultationEnabled;
    private Boolean teleConsultationEnabled;
    private Boolean emergencyAvailable;
}
