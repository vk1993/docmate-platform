package com.docmate.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PatientProfileDto(
    UUID id,
    String fullName,
    String email,
    String phone,
    String dateOfBirth,
    String gender,
    String bloodType,
    String height,
    String weight,
    String medicalHistory,
    String emergencyContactName,
    String emergencyContactPhone,
    AddressDto address,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
