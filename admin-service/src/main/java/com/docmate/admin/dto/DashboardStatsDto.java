package com.docmate.admin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardStatsDto {

    // User Statistics
    private long totalUsers;
    private long totalPatients;
    private long totalDoctors;
    private long totalAdmins;
    private long newUsersThisMonth;

    // Doctor Statistics
    private long approvedDoctors;
    private long pendingDoctorApprovals;
    private long rejectedDoctors;

    // Appointment Statistics
    private long totalAppointments;
    private long todaysAppointments;
    private long pendingAppointments;
    private long completedAppointments;
    private long cancelledAppointments;

    // Payment Statistics
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal todaysRevenue;
    private long completedPayments;
    private long failedPayments;
    private long pendingPayments;

    // System Statistics
    private long totalNotifications;
    private long unreadNotifications;
    private boolean maintenanceMode;
    private String systemVersion;
    private long uptime;
}
