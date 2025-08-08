package com.docmate.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PatientStatsDto(
    Long totalAppointments,
    Long completedAppointments,
    Long cancelledAppointments,
    Long upcomingAppointments,
    Long totalPrescriptions,
    Long totalReports,
    LocalDateTime lastAppointmentDate,
    String preferredDoctorName
) {}
