package com.docmate.common.dto;

import java.util.List;

/**
 * Data transfer object representing a doctor's profile.
 */
public record DoctorProfileDto(
        Long id,
        String fullName,
        String email,
        String phone,
        Integer experience,
        String bio,
        Integer fee,
        Boolean video,
        Boolean tele,
        Boolean emergency,
        List<DoctorAddressDto> addresses,
        List<Long> specializationIds,
        List<Long> conditionIds
) {
}