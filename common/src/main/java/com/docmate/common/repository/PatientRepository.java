package com.docmate.common.repository;

import com.docmate.common.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByUserId(UUID userId);

    @Query("SELECT p FROM Patient p WHERE p.user.email = :email AND p.user.isActive = true")
    Optional<Patient> findByUserEmail(@Param("email") String email);

    @Query("SELECT p FROM Patient p JOIN FETCH p.user WHERE p.id = :id AND p.user.isActive = true")
    Optional<Patient> findByIdWithUser(@Param("id") UUID id);

    @Query("SELECT p FROM Patient p WHERE p.user.isActive = true")
    Page<Patient> findAllActive(Pageable pageable);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.user.isActive = true")
    Long countByUserIsActiveTrue();

    boolean existsByUserId(UUID userId);
}
