package com.docmate.user.service;

import com.docmate.common.entity.Doctor;
import com.docmate.common.entity.User;
import com.docmate.common.exception.BusinessException;
import com.docmate.common.dto.DoctorDto;
import com.docmate.common.dto.DoctorSearchResponse;
import com.docmate.common.dto.DoctorStatsResponse;
import com.docmate.common.dto.UpdateDoctorProfileRequest;
import com.docmate.user.mapper.DoctorMapper;
import com.docmate.common.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    public DoctorDto createDoctorProfile(DoctorDto doctorDto, User user) {
        log.info("Creating doctor profile for user: {}", user.getEmail());

        // Check if doctor profile already exists
        if (doctorRepository.findByUserEmail(user.getEmail()).isPresent()) {
            throw new BusinessException("DOCTOR_PROFILE_EXISTS", "Doctor profile already exists for this user", 409);
        }

        Doctor doctor = doctorMapper.toEntity(doctorDto);
        doctor.setUser(user);
        doctor.setId(user.getId());
        doctor.setIsApproved(false); // Requires admin approval
        doctor.setIsActive(true);

        doctor = doctorRepository.save(doctor);

        log.info("Doctor profile created successfully for user: {} - Pending approval", user.getEmail());
        return doctorMapper.toDto(doctor);
    }

    @Transactional(readOnly = true)
    public DoctorDto getDoctorProfile(UUID doctorId) {
        log.info("Fetching doctor profile with ID: {}", doctorId);

        Doctor doctor = doctorRepository.findByIdWithUser(doctorId)
                .orElseThrow(() -> new BusinessException("DOCTOR_NOT_FOUND", "Doctor not found", 404));

        return doctorMapper.toDto(doctor);
    }

    @Transactional(readOnly = true)
    public DoctorDto getDoctorByEmail(String email) {
        log.info("Fetching doctor profile by email: {}", email);

        Doctor doctor = doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new BusinessException("DOCTOR_NOT_FOUND", "Doctor not found", 404));

        return doctorMapper.toDto(doctor);
    }

    public DoctorDto updateDoctorProfile(UUID doctorId, UpdateDoctorProfileRequest request) {
        log.info("Updating doctor profile with ID: {}", doctorId);

        Doctor existingDoctor = doctorRepository.findByIdWithUser(doctorId)
                .orElseThrow(() -> new BusinessException("DOCTOR_NOT_FOUND", "Doctor not found", 404));

        // Update doctor fields from request
        if (request.getLicenseNumber() != null) {
            existingDoctor.setLicenseNumber(request.getLicenseNumber());
        }
        if (request.getSpecializationId() != null) {
            // Set specialization - would need SpecializationRepository to fetch entity
            // existingDoctor.setSpecialization(specializationRepository.findById(request.getSpecializationId()));
        }
        if (request.getExperienceYears() != null) {
            existingDoctor.setExperienceYears(request.getExperienceYears());
        }
        if (request.getFeePerConsultation() != null) {
            existingDoctor.setFeePerConsultation(request.getFeePerConsultation());
        }
        if (request.getBio() != null) {
            existingDoctor.setBio(request.getBio());
        }
        if (request.getClinicName() != null) {
            existingDoctor.setClinicName(request.getClinicName());
        }
        if (request.getVideoConsultationEnabled() != null) {
            existingDoctor.setVideoConsultationEnabled(request.getVideoConsultationEnabled());
        }
        if (request.getTeleConsultationEnabled() != null) {
            existingDoctor.setTeleConsultationEnabled(request.getTeleConsultationEnabled());
        }
        if (request.getEmergencyAvailable() != null) {
            existingDoctor.setEmergencyAvailable(request.getEmergencyAvailable());
        }

        existingDoctor = doctorRepository.save(existingDoctor);

        log.info("Doctor profile updated successfully for ID: {}", doctorId);
        return doctorMapper.toDto(existingDoctor);
    }

    public DoctorDto approveDoctorProfile(UUID doctorId) {
        log.info("Approving doctor profile with ID: {}", doctorId);

        Doctor doctor = doctorRepository.findByIdWithUser(doctorId)
                .orElseThrow(() -> new BusinessException("DOCTOR_NOT_FOUND", "Doctor not found", 404));

        if (doctor.getIsApproved()) {
            throw new BusinessException("DOCTOR_ALREADY_APPROVED", "Doctor is already approved", 400);
        }

        doctor.setIsApproved(true);
        doctor = doctorRepository.save(doctor);

        log.info("Doctor profile approved successfully for ID: {}", doctorId);
        return doctorMapper.toDto(doctor);
    }

    public DoctorDto rejectDoctorProfile(UUID doctorId) {
        log.info("Rejecting doctor profile with ID: {}", doctorId);

        Doctor doctor = doctorRepository.findByIdWithUser(doctorId)
                .orElseThrow(() -> new BusinessException("DOCTOR_NOT_FOUND", "Doctor not found", 404));

        doctor.setIsApproved(false);
        doctor.setIsActive(false);
        doctor = doctorRepository.save(doctor);

        log.info("Doctor profile rejected for ID: {}", doctorId);
        return doctorMapper.toDto(doctor);
    }

    @Transactional(readOnly = true)
    public Page<DoctorDto> getApprovedDoctors(Pageable pageable) {
        log.info("Fetching approved doctors with pagination");

        Page<Doctor> doctors = doctorRepository.findApprovedDoctors(pageable);
        return doctors.map(doctorMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<DoctorDto> getPendingApprovalDoctors(Pageable pageable) {
        log.info("Fetching pending approval doctors with pagination");

        Page<Doctor> doctors = doctorRepository.findPendingApprovalDoctors(pageable);
        return doctors.map(doctorMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<DoctorDto> getDoctorsBySpecialization(UUID specializationId, Pageable pageable) {
        log.info("Fetching doctors by specialization ID: {}", specializationId);

        Page<Doctor> doctors = doctorRepository.findBySpecializationId(specializationId, pageable);
        return doctors.map(doctorMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<DoctorSearchResponse> searchDoctors(String query, UUID specializationId, UUID conditionId,
                                                   BigDecimal maxFee, String consultationType, Pageable pageable) {
        log.info("Searching doctors with query: {}, specializationId: {}, conditionId: {}, maxFee: {}, consultationType: {}",
                query, specializationId, conditionId, maxFee, consultationType);

        return doctorRepository.searchDoctors(query, specializationId, conditionId, maxFee, consultationType, pageable);
    }

    @Transactional(readOnly = true)
    public DoctorStatsResponse getDoctorStats(UUID doctorId) {
        log.info("Getting doctor statistics for ID: {}", doctorId);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new BusinessException("DOCTOR_NOT_FOUND", "Doctor not found", 404));

        // These would typically be calculated from appointment and payment data
        // For now, returning placeholder data
        return DoctorStatsResponse.builder()
                .totalAppointments(0L)
                .completedAppointments(0L)
                .pendingAppointments(0L)
                .cancelledAppointments(0L)
                .totalPatients(0L)
                .averageRating(doctor.getAverageRating() != null ? doctor.getAverageRating().doubleValue() : 0.0)
                .totalReviews(doctor.getReviewCount() != null ? doctor.getReviewCount().longValue() : 0L)
                .totalEarnings(0L)
                .thisMonthAppointments(0L)
                .thisMonthEarnings(0L)
                .build();
    }

    public void deleteDoctor(UUID doctorId) {
        log.info("Deleting doctor with ID: {}", doctorId);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new BusinessException("DOCTOR_NOT_FOUND", "Doctor not found", 404));

        // Soft delete by setting inactive
        doctor.setIsActive(false);
        doctorRepository.save(doctor);

        log.info("Doctor deleted (deactivated) successfully for ID: {}", doctorId);
    }

    @Transactional(readOnly = true)
    public long getApprovedDoctorCount() {
        return doctorRepository.countByIsApprovedTrueAndIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public long getPendingApprovalDoctorCount() {
        return doctorRepository.countByIsApprovedFalseAndIsActiveTrue();
    }
}
