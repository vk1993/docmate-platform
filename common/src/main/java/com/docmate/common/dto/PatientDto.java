package com.docmate.common.dto;

import com.docmate.common.enums.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientDto {

    private UUID id;
    private UserDto user;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String bloodType;
    private String height;
    private String weight;
    private AddressDto address;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
