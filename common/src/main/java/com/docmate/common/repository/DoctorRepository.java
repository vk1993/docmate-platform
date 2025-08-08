package com.docmate.common.repository;

import com.docmate.common.entity.Doctor;
import com.docmate.common.dto.DoctorSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    List<Doctor> findByIsApprovedTrue();

    List<Doctor> findByIsApprovedTrueAndIsActiveTrue();

    @Query("SELECT d FROM Doctor d WHERE d.isApproved = true AND d.isActive = true AND d.specialization.id = :specializationId")
    List<Doctor> findApprovedDoctorsBySpecialization(@Param("specializationId") UUID specializationId);

    @Query("SELECT d FROM Doctor d WHERE d.isApproved = :approved")
    List<Doctor> findByApprovalStatus(@Param("approved") boolean approved);
    @Query("SELECT d FROM Doctor d JOIN FETCH d.user WHERE d.user.email = :email AND d.user.isActive = true")
    Optional<Doctor> findByUserEmail(@Param("email") String email);

    @Query("SELECT d FROM Doctor d JOIN FETCH d.user WHERE d.id = :id AND d.user.isActive = true")
    Optional<Doctor> findByIdWithUser(@Param("id") UUID id);

    @Query("SELECT d FROM Doctor d JOIN FETCH d.user WHERE d.isApproved = true AND d.isActive = true")
    Page<Doctor> findApprovedDoctors(Pageable pageable);

    @Query("SELECT d FROM Doctor d JOIN FETCH d.user WHERE d.isApproved = false AND d.isActive = true")
    Page<Doctor> findPendingApprovalDoctors(Pageable pageable);

    @Query("SELECT d FROM Doctor d JOIN FETCH d.specialization WHERE d.specialization.id = :specializationId AND d.isApproved = true AND d.isActive = true")
    Page<Doctor> findBySpecializationId(@Param("specializationId") UUID specializationId, Pageable pageable);

    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.isApproved = true AND d.isActive = true")
    long countByIsApprovedTrueAndIsActiveTrue();

    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.isApproved = false AND d.isActive = true")
    long countByIsApprovedFalseAndIsActiveTrue();

    @Query("""
        SELECT new com.docmate.common.dto.DoctorSearchResponse(
            d.id, d.user.fullName, d.user.profilePicture, s.name, d.bio, 
            d.experienceYears, d.feePerConsultation, d.averageRating, d.reviewCount,
            d.clinicName, d.videoConsultationEnabled, d.teleConsultationEnabled, d.emergencyAvailable
        )
        FROM Doctor d 
        LEFT JOIN d.user u 
        LEFT JOIN d.specialization s
        LEFT JOIN d.conditions c
        WHERE d.isApproved = true AND d.isActive = true
        AND (:query IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%')) 
             OR LOWER(d.bio) LIKE LOWER(CONCAT('%', :query, '%'))
             OR LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')))
        AND (:specializationId IS NULL OR s.id = :specializationId)
        AND (:conditionId IS NULL OR c.id = :conditionId)
        AND (:maxFee IS NULL OR d.feePerConsultation <= :maxFee)
        AND (:consultationType IS NULL 
             OR (:consultationType = 'VIDEO' AND d.videoConsultationEnabled = true)
             OR (:consultationType = 'TELE' AND d.teleConsultationEnabled = true))
        ORDER BY d.averageRating DESC, d.reviewCount DESC
        """)
    Page<DoctorSearchResponse> searchDoctors(@Param("query") String query,
                                             @Param("specializationId") UUID specializationId,
                                             @Param("conditionId") UUID conditionId,
                                             @Param("maxFee") BigDecimal maxFee,
                                             @Param("consultationType") String consultationType,
                                             Pageable pageable);

    Optional<Doctor> findByUserId(UUID userId);

    @Query("SELECT d FROM Doctor d JOIN FETCH d.user WHERE d.user.isActive = :isActive")
    List<Doctor> findByUserIsActive(@Param("isActive") Boolean isActive);

    @Query("SELECT d FROM Doctor d JOIN FETCH d.user WHERE d.user.isActive = :isActive")
    Page<Doctor> findByUserIsActive(@Param("isActive") Boolean isActive, Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.feePerConsultation BETWEEN :minFee AND :maxFee")
    Page<Doctor> findByConsultationFeeBetween(@Param("minFee") BigDecimal minFee,
                                              @Param("maxFee") BigDecimal maxFee,
                                              Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.averageRating >= :rating")
    Page<Doctor> findByMinimumRating(@Param("rating") BigDecimal rating, Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.videoConsultationEnabled = true AND d.isApproved = true AND d.isActive = true")
    Page<Doctor> findVideoConsultationEnabledDoctors(Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.teleConsultationEnabled = true AND d.isApproved = true AND d.isActive = true")
    Page<Doctor> findTeleConsultationEnabledDoctors(Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.emergencyAvailable = true AND d.isApproved = true AND d.isActive = true")
    Page<Doctor> findEmergencyAvailableDoctors(Pageable pageable);
}
