package com.docmate.common.dto;

import java.util.List;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for updating a doctor's own profile.
 */
public record UpdateDoctorProfileDto(
        @NotNull Integer experience,
        String bio,
        @NotNull Integer fee,
        Boolean video,
        Boolean tele,
        Boolean emergency,
        List<DoctorAddressDto> addresses,
        List<Long> specializationIds,
        List<Long> conditionIds
) {
}