package com.docmate.user.service;

import com.docmate.common.dto.DoctorVerificationDto;
import com.docmate.common.dto.SubmitVerificationRequest;
import com.docmate.common.entity.Doctor;
import com.docmate.common.entity.DoctorVerificationDocuments;
import com.docmate.common.enums.VerificationStatus;
import com.docmate.common.exception.BusinessException;
import com.docmate.common.repository.DoctorRepository;
import com.docmate.common.repository.DoctorVerificationDocumentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DoctorVerificationService {

    private final DoctorVerificationDocumentsRepository verificationRepository;
    private final DoctorRepository doctorRepository;

    public DoctorVerificationDto submitVerificationDocuments(SubmitVerificationRequest request) {
        log.info("Submitting verification documents for doctor: {}", request.getDoctorId());

        // Validate doctor exists
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new BusinessException("DOCTOR_NOT_FOUND", "Doctor not found", 404));

        // Check if verification already exists
        if (verificationRepository.existsByDoctorId(request.getDoctorId())) {
            throw new BusinessException("VERIFICATION_ALREADY_EXISTS", 
                "Verification documents already submitted for this doctor", 400);
        }

        // Create verification documents record
        DoctorVerificationDocuments verification = DoctorVerificationDocuments.builder()
                .doctor(doctor)
                .licenseDocumentUrl(request.getLicenseDocumentUrl())
                .idDocumentUrl(request.getIdDocumentUrl())
                .verificationStatus(VerificationStatus.PENDING)
                .build();

        verification = verificationRepository.save(verification);
        log.info("Verification documents submitted successfully for doctor: {}", request.getDoctorId());

        return mapToDto(verification);
    }

    @Transactional(readOnly = true)
    public DoctorVerificationDto getDoctorVerification(UUID doctorId) {
        log.info("Fetching verification for doctor: {}", doctorId);

        DoctorVerificationDocuments verification = verificationRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new BusinessException("VERIFICATION_NOT_FOUND", 
                    "No verification documents found for this doctor", 404));

        return mapToDto(verification);
    }

    @Transactional(readOnly = true)
    public List<DoctorVerificationDto> getDoctorVerificationsByStatus(VerificationStatus status) {
        log.info("Fetching verifications with status: {}", status);

        List<DoctorVerificationDocuments> verifications = verificationRepository.findByVerificationStatus(status);
        return verifications.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private DoctorVerificationDto mapToDto(DoctorVerificationDocuments verification) {
        return DoctorVerificationDto.builder()
                .id(verification.getId())
                .doctorId(verification.getDoctor().getId())
                .doctorName(verification.getDoctor().getUser().getFullName())
                .licenseDocumentUrl(verification.getLicenseDocumentUrl())
                .idDocumentUrl(verification.getIdDocumentUrl())
                .verificationStatus(verification.getVerificationStatus())
                .verifiedById(verification.getVerifiedBy() != null ? verification.getVerifiedBy().getId() : null)
                .verifiedByName(verification.getVerifiedBy() != null ? verification.getVerifiedBy().getFullName() : null)
                .verifiedDate(verification.getVerifiedDate())
                .rejectionReason(verification.getRejectionReason())
                .submittedDate(verification.getCreatedDate())
                .build();
    }
}
