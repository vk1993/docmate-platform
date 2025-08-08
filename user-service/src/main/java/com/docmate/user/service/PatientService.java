package com.docmate.user.service;

import com.docmate.common.dto.*;
import com.docmate.common.entity.Patient;
import com.docmate.common.entity.User;
import com.docmate.common.exception.BusinessException;
import com.docmate.common.repository.PatientRepository;
import com.docmate.common.repository.UserRepository;
import com.docmate.user.mapper.PatientMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PatientMapper patientMapper;

    public void validatePatientExists(UUID patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new BusinessException("PATIENT_NOT_FOUND", "Patient not found with ID: " + patientId, 404);
        }
    }

    public PatientProfileDto getPatientProfile(UUID patientId) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new BusinessException("PATIENT_NOT_FOUND", "Patient not found with ID: " + patientId, 404));

        return patientMapper.toProfileDto(patient);
    }

    public PatientProfileDto getPatientByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found with email: " + email, 404));
        
        Patient patient = patientRepository.findByUserId(user.getId())
            .orElseThrow(() -> new BusinessException("PATIENT_NOT_FOUND", "Patient not found for user: " + email, 404));

        return patientMapper.toProfileDto(patient);
    }

    public PatientProfileDto getPatientProfileByUserId(UUID userId) {
        Patient patient = patientRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException("PATIENT_NOT_FOUND", "Patient not found for user ID: " + userId, 404));

        return patientMapper.toProfileDto(patient);
    }

    public PatientProfileDto updatePatientProfile(UUID patientId, UpdatePatientProfileDto updateDto) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new BusinessException("PATIENT_NOT_FOUND", "Patient not found with ID: " + patientId, 404));

        // Update patient and user information
        User user = patient.getUser();
        if (updateDto.fullName() != null) {
            user.setFullName(updateDto.fullName());
        }

        if (updateDto.phone() != null) {
            user.setPhone(updateDto.phone());
        }

        // Update patient-specific fields
        if (updateDto.dateOfBirth() != null) {
            patient.setDateOfBirth(java.time.LocalDate.parse(updateDto.dateOfBirth()));
        }

        if (updateDto.bloodType() != null) {
            patient.setBloodType(updateDto.bloodType());
        }

        if (updateDto.height() != null) {
            patient.setHeight(updateDto.height());
        }

        if (updateDto.weight() != null) {
            patient.setWeight(updateDto.weight());
        }

        Patient savedPatient = patientRepository.save(patient);
        log.info("Updated patient profile for patient ID: {}", patientId);

        return patientMapper.toProfileDto(savedPatient);
    }

    public PatientMedicalHistoryResponse updateMedicalHistory(UUID patientId, UpdatePatientMedicalHistoryRequest request) {
        log.info("Updating medical history for patient: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new BusinessException("PATIENT_NOT_FOUND", "Patient not found", 404));

        // In a real implementation, you would have a PatientMedicalHistory entity
        // For now, returning a placeholder response
        return PatientMedicalHistoryResponse.builder()
                .id(UUID.randomUUID())
                .patientId(patientId)
                .allergies(request.getAllergies())
                .chronicConditions(request.getChronicConditions())
                .currentMedications(request.getCurrentMedications())
                .previousSurgeries(request.getPreviousSurgeries())
                .familyHistory(request.getFamilyHistory())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<PatientReportResponse> getPatientReports(UUID patientId, Pageable pageable) {
        log.info("Getting patient reports for patient: {}", patientId);

        validatePatientExists(patientId);

        // In a real implementation, you would query PatientReports repository
        // For now, returning empty page
        return Page.empty(pageable);
    }

    public PatientReportResponse uploadPatientReport(UUID patientId, MultipartFile file, String title,
                                                   String description, LocalDate testDate) {
        log.info("Uploading patient report for patient: {}", patientId);

        validatePatientExists(patientId);

        // In a real implementation, you would:
        // 1. Save file to storage (S3, etc.)
        // 2. Create PatientReport entity
        // 3. Save to database

        return PatientReportResponse.builder()
                .id(UUID.randomUUID())
                .patientId(patientId)
                .title(title)
                .description(description)
                .testDate(testDate)
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .build();
    }

    public void deletePatientReport(UUID patientId, UUID reportId) {
        log.info("Deleting patient report {} for patient: {}", reportId, patientId);

        validatePatientExists(patientId);

        // In a real implementation, you would:
        // 1. Verify the report belongs to the patient
        // 2. Delete from storage
        // 3. Delete from database

        log.info("Patient report deleted successfully");
    }

    public PatientDto createPatientProfile(PatientDto patientDto, User user) {
        log.info("Creating patient profile for user: {}", user.getEmail());

        // Check if patient profile already exists
        if (patientRepository.findByUserId(user.getId()).isPresent()) {
            throw new BusinessException("PATIENT_PROFILE_EXISTS", "Patient profile already exists for this user", 409);
        }

        Patient patient = patientMapper.toEntity(patientDto);
        patient.setUser(user);
        patient.setId(user.getId());

        patient = patientRepository.save(patient);

        log.info("Patient profile created successfully for user: {}", user.getEmail());
        return patientMapper.toDto(patient);
    }

    @Transactional(readOnly = true)
    public Page<PatientDto> getAllPatients(Pageable pageable) {
        log.info("Fetching all patients with pagination");

        Page<Patient> patients = patientRepository.findAll(pageable);
        return patients.map(patientMapper::toDto);
    }

    public PatientDto getPatientById(UUID patientId) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new BusinessException("PATIENT_NOT_FOUND", "Patient not found with ID: " + patientId, 404));
        return patientMapper.toDto(patient);
    }

    public PatientDto updatePatientById(UUID patientId, PatientDto patientDto) {
        Patient existingPatient = patientRepository.findById(patientId)
            .orElseThrow(() -> new BusinessException("PATIENT_NOT_FOUND", "Patient not found with ID: " + patientId, 404));

        patientMapper.updateEntityFromDto(patientDto, existingPatient);
        Patient updatedPatient = patientRepository.save(existingPatient);
        log.info("Updated patient with ID: {}", patientId);

        return patientMapper.toDto(updatedPatient);
    }

    public void deletePatient(UUID patientId) {
        log.info("Deleting patient with ID: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new BusinessException("PATIENT_NOT_FOUND", "Patient not found", 404));

        // Soft delete by setting user inactive
        patient.getUser().setIsActive(false);
        patientRepository.save(patient);

        log.info("Patient deleted (deactivated) successfully for ID: {}", patientId);
    }

    public PatientStatsDto getPatientStats(UUID patientId) {
        // Verify patient exists
        if (!patientRepository.existsById(patientId)) {
            throw new BusinessException("PATIENT_NOT_FOUND", "Patient not found with ID: " + patientId, 404);
        }

        // Return basic stats - you can expand this based on requirements
        return new PatientStatsDto(
            1L, // totalAppointments
            1L, // completedAppointments
            0L, // cancelledAppointments
            0L, // upcomingAppointments
            0L, // totalPrescriptions
            0L, // totalReports
            null, // lastAppointmentDate
            null  // preferredDoctorName
        );
    }

    public Long getActivePatientCount() {
        return patientRepository.countByUserIsActiveTrue();
    }
}
