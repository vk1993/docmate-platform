package com.docmate.appointment.dto;

import com.docmate.common.enums.ConsultationMode;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class CreateAppointmentRequest {
    
    @NotNull(message = "Doctor ID is required")
    private UUID doctorId;
    
    @NotNull(message = "Patient ID is required")
    private UUID patientId;
    
    @NotNull(message = "Appointment date and time is required")
    @Future(message = "Appointment must be scheduled for a future date and time")
    private LocalDateTime appointmentDateTime;
    
    @NotNull(message = "Consultation mode is required")
    private ConsultationMode consultationMode;
    
    @Positive(message = "Duration must be positive")
    @Builder.Default
    private Integer durationMinutes = 30;
    
    @Positive(message = "Consultation fee must be positive")
    private BigDecimal consultationFee;
    
    private String reasonForVisit;
    
    private String symptoms;
    
    private String notes;
}
