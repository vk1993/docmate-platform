package com.docmate.appointment.dto;

import com.docmate.common.enums.AppointmentStatus;
import com.docmate.common.enums.ConsultationMode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentDto {

    private UUID id;
    private DoctorSummaryDto doctor;
    private PatientSummaryDto patient;
    private LocalDateTime appointmentDateTime;
    private ConsultationMode consultationMode;
    private AppointmentStatus status;
    private Integer durationMinutes;
    private BigDecimal consultationFee;
    private String reasonForVisit;
    private String symptoms;
    private String notes;
    private UUID prescriptionId;
    private Boolean followUpRequired;
    private LocalDateTime followUpDate;
    private String cancelledReason;
    private UUID cancelledBy;
    private LocalDateTime cancelledAt;
    private LocalDateTime completedAt;
    private Integer rating;
    private String review;
    private UUID paymentId;
    private String meetingLink;
    private String meetingId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
