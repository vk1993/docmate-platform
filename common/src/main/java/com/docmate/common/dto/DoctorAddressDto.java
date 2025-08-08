package com.docmate.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Address data for a doctor registration or profile.
 */
public record DoctorAddressDto(
        @NotBlank String line1,
        String line2,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String country,
        @NotBlank String postalCode,
        Boolean isPrimary
) {
}