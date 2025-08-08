package com.docmate.appointment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorSummaryDto {
    
    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private String licenseNumber;
    private Integer experienceYears;
    private BigDecimal feePerConsultation;
    private String clinicName;
    private String specialization;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private Boolean videoConsultationEnabled;
    private Boolean teleConsultationEnabled;
}
