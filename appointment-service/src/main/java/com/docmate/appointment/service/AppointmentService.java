package com.docmate.appointment.service;

import com.docmate.appointment.dto.AppointmentDto;
import com.docmate.appointment.dto.CreateAppointmentRequest;
import com.docmate.appointment.entity.Appointment;
import com.docmate.appointment.mapper.AppointmentMapper;
import com.docmate.appointment.repository.AppointmentRepository;
import com.docmate.common.enums.AppointmentStatus;
import com.docmate.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AvailabilityService availabilityService;

    public AppointmentDto createAppointment(CreateAppointmentRequest request) {
        log.info("Creating appointment for patient {} with doctor {}", request.getPatientId(), request.getDoctorId());

        // Validate doctor and patient exist
        doctorService.validateDoctorExists(request.getDoctorId());
        patientService.validatePatientExists(request.getPatientId());

        // Check doctor availability
        if (!availabilityService.isDoctorAvailable(request.getDoctorId(), request.getAppointmentDateTime(), request.getDurationMinutes())) {
            throw new BusinessException("DOCTOR_NOT_AVAILABLE", "Doctor is not available at the requested time", 400);
        }

        // Check for conflicting appointments
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
            request.getDoctorId(),
            request.getAppointmentDateTime(),
            request.getAppointmentDateTime().plusMinutes(request.getDurationMinutes())
        );

        if (!conflicts.isEmpty()) {
            throw new BusinessException("APPOINTMENT_CONFLICT", "Doctor already has an appointment at this time", 409);
        }

        Appointment appointment = Appointment.builder()
            .patientId(request.getPatientId())
            .doctorId(request.getDoctorId())
            .appointmentDateTime(request.getAppointmentDateTime())
            .consultationMode(request.getConsultationMode())
            .durationMinutes(request.getDurationMinutes())
            .consultationFee(request.getConsultationFee())
            .reasonForVisit(request.getReasonForVisit())
            .symptoms(request.getSymptoms())
            .status(AppointmentStatus.SCHEDULED)
            .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Created appointment with ID: {}", savedAppointment.getId());

        return appointmentMapper.toDto(savedAppointment);
    }

    public AppointmentDto getAppointment(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new BusinessException("APPOINTMENT_NOT_FOUND", "Appointment not found with ID: " + appointmentId, 404));
        return appointmentMapper.toDto(appointment);
    }

    public Page<AppointmentDto> getPatientAppointments(UUID patientId, Pageable pageable) {
        Page<Appointment> appointments = appointmentRepository.findByPatientIdOrderByAppointmentDateTimeDesc(patientId, pageable);
        return appointments.map(appointmentMapper::toDto);
    }

    public Page<AppointmentDto> getDoctorAppointments(UUID doctorId, Pageable pageable) {
        Page<Appointment> appointments = appointmentRepository.findByDoctorIdOrderByAppointmentDateTimeDesc(doctorId, pageable);
        return appointments.map(appointmentMapper::toDto);
    }

    public List<AppointmentDto> getUpcomingAppointments(UUID userId, boolean isDoctor) {
        LocalDateTime now = LocalDateTime.now();
        List<Appointment> appointments;
        
        if (isDoctor) {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentDateTimeAfterOrderByAppointmentDateTime(userId, now);
        } else {
            appointments = appointmentRepository.findByPatientIdAndAppointmentDateTimeAfterOrderByAppointmentDateTime(userId, now);
        }
        
        return appointments.stream()
            .map(appointmentMapper::toDto)
            .toList();
    }

    public AppointmentDto confirmAppointment(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new BusinessException("APPOINTMENT_NOT_FOUND", "Appointment not found with ID: " + appointmentId, 404));

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BusinessException("INVALID_STATUS", "Only scheduled appointments can be confirmed", 400);
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Confirmed appointment with ID: {}", appointmentId);

        return appointmentMapper.toDto(savedAppointment);
    }

    public AppointmentDto cancelAppointment(UUID appointmentId, String reason, UUID cancelledBy) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new BusinessException("APPOINTMENT_NOT_FOUND", "Appointment not found with ID: " + appointmentId, 404));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED || appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessException("INVALID_STATUS", "Cannot cancel completed or already cancelled appointment", 400);
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelledReason(reason);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Cancelled appointment with ID: {} by user: {}", appointmentId, cancelledBy);

        return appointmentMapper.toDto(savedAppointment);
    }

    public AppointmentDto completeAppointment(UUID appointmentId, String notes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new BusinessException("APPOINTMENT_NOT_FOUND", "Appointment not found with ID: " + appointmentId, 404));

        if (appointment.getStatus() != AppointmentStatus.IN_PROGRESS && appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BusinessException("INVALID_STATUS", "Only confirmed or in-progress appointments can be completed", 400);
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        if (notes != null) {
            appointment.setNotes(notes);
        }
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Completed appointment with ID: {}", appointmentId);

        return appointmentMapper.toDto(savedAppointment);
    }

    public void validateAppointmentExists(UUID appointmentId) {
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new BusinessException("APPOINTMENT_NOT_FOUND", "Appointment not found with ID: " + appointmentId, 404);
        }
    }

    public List<AppointmentDto> getAppointmentsByStatus(AppointmentStatus status) {
        List<Appointment> appointments = appointmentRepository.findByStatusOrderByAppointmentDateTime(status);
        return appointments.stream()
            .map(appointmentMapper::toDto)
            .toList();
    }

    public List<AppointmentDto> getTodaysAppointments(UUID doctorId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentDateTimeBetweenOrderByAppointmentDateTime(
            doctorId, startOfDay, endOfDay);
        
        return appointments.stream()
            .map(appointmentMapper::toDto)
            .toList();
    }
}
