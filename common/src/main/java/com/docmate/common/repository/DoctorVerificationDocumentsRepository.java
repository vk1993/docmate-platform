package com.docmate.common.repository;

import com.docmate.common.entity.DoctorVerificationDocuments;
import com.docmate.common.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorVerificationDocumentsRepository extends JpaRepository<DoctorVerificationDocuments, UUID> {

    Optional<DoctorVerificationDocuments> findByDoctorId(UUID doctorId);

    List<DoctorVerificationDocuments> findByVerificationStatus(VerificationStatus status);

    List<DoctorVerificationDocuments> findByVerifiedById(UUID verifiedById);

    @Query("SELECT dvd FROM DoctorVerificationDocuments dvd WHERE dvd.verificationStatus = :status ORDER BY dvd.createdDate ASC")
    List<DoctorVerificationDocuments> findPendingVerificationsOrderByDate(@Param("status") VerificationStatus status);

    @Query("SELECT COUNT(dvd) FROM DoctorVerificationDocuments dvd WHERE dvd.verificationStatus = :status")
    long countByVerificationStatus(@Param("status") VerificationStatus status);

    boolean existsByDoctorId(UUID doctorId);
}
