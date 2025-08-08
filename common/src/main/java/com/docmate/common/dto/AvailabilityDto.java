package com.docmate.common.dto;

import com.docmate.common.enums.DayOfWeek;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AvailabilityDto {

    private UUID id;
    private UUID doctorId;
    private DayOfWeek dayOfWeek; // For recurring availability
    private LocalDate date; // For adhoc availability
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer capacity;
    private Boolean isActive;
    private Boolean isRecurring;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
