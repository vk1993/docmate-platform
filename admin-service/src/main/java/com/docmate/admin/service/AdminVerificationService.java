package com.docmate.admin.service;

import com.docmate.common.dto.DoctorVerificationDto;
import com.docmate.common.dto.VerificationReviewRequest;
import com.docmate.common.entity.DoctorVerificationDocuments;
import com.docmate.common.entity.User;
import com.docmate.common.enums.VerificationStatus;
import com.docmate.common.exception.BusinessException;
import com.docmate.common.repository.DoctorRepository;
import com.docmate.common.repository.DoctorVerificationDocumentsRepository;
import com.docmate.common.repository.CommonUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminVerificationService {

    private final DoctorVerificationDocumentsRepository verificationRepository;
    private final DoctorRepository doctorRepository;
    private final CommonUserRepository userRepository;

    @Transactional(readOnly = true)
    public List<DoctorVerificationDto> getPendingVerifications() {
        log.info("Fetching pending doctor verifications");

        List<DoctorVerificationDocuments> verifications =
            verificationRepository.findPendingVerificationsOrderByDate(VerificationStatus.PENDING);

        return verifications.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DoctorVerificationDto getVerificationById(UUID verificationId) {
        log.info("Fetching verification details for ID: {}", verificationId);

        DoctorVerificationDocuments verification = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new BusinessException("VERIFICATION_NOT_FOUND",
                    "Verification not found", 404));

        return mapToDto(verification);
    }

    public DoctorVerificationDto reviewVerification(VerificationReviewRequest request, UUID adminUserId) {
        log.info("Processing verification review for ID: {} by admin: {}",
            request.getVerificationId(), adminUserId);

        // Validate admin user exists
        User adminUser = userRepository.findById(adminUserId)
                .orElseThrow(() -> new BusinessException("ADMIN_NOT_FOUND", "Admin user not found", 404));

        // Get verification document
        DoctorVerificationDocuments verification = verificationRepository.findById(request.getVerificationId())
                .orElseThrow(() -> new BusinessException("VERIFICATION_NOT_FOUND",
                    "Verification not found", 404));

        // Validate current status
        if (verification.getVerificationStatus() != VerificationStatus.PENDING) {
            throw new BusinessException("VERIFICATION_ALREADY_PROCESSED",
                "This verification has already been processed", 400);
        }

        // Validate rejection reason if rejecting
        if (request.getVerificationStatus() == VerificationStatus.REJECTED &&
            (request.getRejectionReason() == null || request.getRejectionReason().trim().isEmpty())) {
            throw new BusinessException("REJECTION_REASON_REQUIRED",
                "Rejection reason is required when rejecting verification", 400);
        }

        // Update verification status
        verification.setVerificationStatus(request.getVerificationStatus());
        verification.setVerifiedBy(adminUser);
        verification.setVerifiedDate(LocalDateTime.now());

        if (request.getVerificationStatus() == VerificationStatus.REJECTED) {
            verification.setRejectionReason(request.getRejectionReason());
        }

        // If approved, update doctor's approval status
        if (request.getVerificationStatus() == VerificationStatus.APPROVED) {
            verification.getDoctor().setIsApproved(true);
            doctorRepository.save(verification.getDoctor());
            log.info("Doctor approved: {}", verification.getDoctor().getId());
        }

        verification = verificationRepository.save(verification);

        log.info("Verification {} {} by admin {}",
            verification.getId(),
            request.getVerificationStatus().name().toLowerCase(),
            adminUserId);

        return mapToDto(verification);
    }

    @Transactional(readOnly = true)
    public List<DoctorVerificationDto> getVerificationsByStatus(VerificationStatus status) {
        log.info("Fetching verifications with status: {}", status);

        List<DoctorVerificationDocuments> verifications = verificationRepository.findByVerificationStatus(status);
        return verifications.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getPendingVerificationCount() {
        return verificationRepository.countByVerificationStatus(VerificationStatus.PENDING);
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
