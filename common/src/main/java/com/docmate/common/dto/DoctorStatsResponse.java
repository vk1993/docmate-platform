package com.docmate.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorStatsResponse {

    private Long totalAppointments;
    private Long completedAppointments;
    private Long pendingAppointments;
    private Long cancelledAppointments;
    private Long totalPatients;
    private Double averageRating;
    private Long totalReviews;
    private Long totalEarnings;
    private Long thisMonthAppointments;
    private Long thisMonthEarnings;
}
