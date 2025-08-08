package com.docmate.common.dto;

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
public class DoctorSearchResponse {

    private UUID id;
    private String fullName;
    private String profilePicture;
    private String specialization;
    private String bio;
    private Integer experienceYears;
    private BigDecimal feePerConsultation;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private String clinicName;
    private Boolean videoConsultationEnabled;
    private Boolean teleConsultationEnabled;
    private Boolean emergencyAvailable;
}
