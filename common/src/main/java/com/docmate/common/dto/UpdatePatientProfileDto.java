package com.docmate.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdatePatientProfileDto(
    @NotBlank(message = "Full name is required")
    String fullName,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phone,

    String dateOfBirth,
    String gender,
    String bloodType,
    String height,
    String weight,
    String emergencyContactName,
    String emergencyContactPhone,
    AddressDto address
) {}
