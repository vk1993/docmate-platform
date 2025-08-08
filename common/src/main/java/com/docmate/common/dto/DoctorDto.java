package com.docmate.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorDto {

    private UUID id;
    private UserDto user;
    private SpecializationDto specialization;
    private String licenseNumber;
    private Integer experienceYears;
    private BigDecimal feePerConsultation;
    private String bio;
    private Boolean videoConsultationEnabled;
    private Boolean teleConsultationEnabled;
    private Boolean emergencyAvailable;
    private Boolean isApproved;
    private Boolean isActive;
    private String clinicName;
    private AddressDto primaryAddress;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private Set<SpecializationDto> specializations;
    private Set<ConditionDto> conditions;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
