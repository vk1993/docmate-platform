package com.docmate.common.entity;

import com.docmate.common.enums.SlotStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "time_slots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlot extends BaseEntity {

    @Column(name = "doctor_id", nullable = false)
    private UUID doctorId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private SlotStatus status = SlotStatus.AVAILABLE;

    @Column(name = "appointment_id")
    private UUID appointmentId;

    @Column(name = "is_emergency_slot")
    @Builder.Default
    private Boolean isEmergencySlot = false;

    @Column(name = "notes")
    private String notes;

    @Column(name = "blocked_reason")
    private String blockedReason;
}
