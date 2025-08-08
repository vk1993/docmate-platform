package com.docmate.appointment.repository;

import com.docmate.appointment.entity.Appointment;
import com.docmate.common.enums.AppointmentStatus;
import com.docmate.common.enums.ConsultationMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    // Find appointments by patient with ordering
    Page<Appointment> findByPatientIdOrderByAppointmentDateTimeDesc(UUID patientId, Pageable pageable);

    // Find appointments by doctor with ordering
    Page<Appointment> findByDoctorIdOrderByAppointmentDateTimeDesc(UUID doctorId, Pageable pageable);

    // Find appointments by status with ordering
    List<Appointment> findByStatusOrderByAppointmentDateTime(AppointmentStatus status);
    Page<Appointment> findByStatusOrderByAppointmentDateTime(AppointmentStatus status, Pageable pageable);

    // Find upcoming appointments for doctor
    List<Appointment> findByDoctorIdAndAppointmentDateTimeAfterOrderByAppointmentDateTime(UUID doctorId, LocalDateTime dateTime);

    // Find upcoming appointments for patient
    List<Appointment> findByPatientIdAndAppointmentDateTimeAfterOrderByAppointmentDateTime(UUID patientId, LocalDateTime dateTime);

    // Find appointments by date range for doctor
    List<Appointment> findByDoctorIdAndAppointmentDateTimeBetweenOrderByAppointmentDateTime(
        UUID doctorId, LocalDateTime startTime, LocalDateTime endTime);

    // Find appointments by date range for patient
    List<Appointment> findByPatientIdAndAppointmentDateTimeBetweenOrderByAppointmentDateTime(
        UUID patientId, LocalDateTime startTime, LocalDateTime endTime);

    // Find conflicting appointments for scheduling validation
    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId " +
           "AND a.status NOT IN ('CANCELLED', 'COMPLETED') " +
           "AND ((a.appointmentDateTime <= :startTime AND :startTime < DATE_ADD(a.appointmentDateTime, INTERVAL a.durationMinutes MINUTE)) " +
           "OR (:startTime <= a.appointmentDateTime AND a.appointmentDateTime < :endTime))")
    List<Appointment> findConflictingAppointments(@Param("doctorId") UUID doctorId,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    // Find appointments by consultation mode
    Page<Appointment> findByConsultationModeOrderByAppointmentDateTime(ConsultationMode consultationMode, Pageable pageable);

    // Find appointments for today for a doctor
    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId " +
           "AND DATE(a.appointmentDateTime) = CURRENT_DATE " +
           "ORDER BY a.appointmentDateTime")
    List<Appointment> findTodaysAppointmentsByDoctor(@Param("doctorId") UUID doctorId);

    // Count appointments by status
    long countByStatus(AppointmentStatus status);

    // Count appointments by doctor
    long countByDoctorId(UUID doctorId);

    // Count appointments by patient
    long countByPatientId(UUID patientId);

    // Find appointments by multiple statuses
    @Query("SELECT a FROM Appointment a WHERE a.status IN :statuses ORDER BY a.appointmentDateTime")
    List<Appointment> findByStatusIn(@Param("statuses") List<AppointmentStatus> statuses);

    // Find appointments that need follow-up
    @Query("SELECT a FROM Appointment a WHERE a.followUpRequired = true AND a.followUpDate IS NOT NULL " +
           "AND a.followUpDate <= :date ORDER BY a.followUpDate")
    List<Appointment> findAppointmentsNeedingFollowUp(@Param("date") LocalDateTime date);
}
