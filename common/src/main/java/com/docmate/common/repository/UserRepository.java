package com.docmate.common.repository;

import com.docmate.common.entity.User;
import com.docmate.common.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role = :role")
    Page<User> findByRole(@Param("role") String role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            @Param("searchTerm") String searchTerm1,
            @Param("searchTerm") String searchTerm2,
            @Param("searchTerm") String searchTerm3,
            Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
           "(:query IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:specialization IS NULL OR :specialization = '') AND " +
           "(:condition IS NULL OR :condition = '') AND " +
           "(:maxFee IS NULL OR :maxFee > 0) AND " +
           "(:consultationMode IS NULL) AND " +
           "(:emergencyAvailable IS NULL OR :emergencyAvailable = true OR :emergencyAvailable = false) AND " +
           "u.role = 'DOCTOR' AND u.isActive = true")
    Page<User> findDoctorsWithFilters(
            @Param("query") String query,
            @Param("specialization") String specialization,
            @Param("condition") String condition,
            @Param("maxFee") BigDecimal maxFee,
            @Param("consultationMode") Object consultationMode,
            @Param("emergencyAvailable") Boolean emergencyAvailable,
            Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = 'DOCTOR' AND u.isActive = true")
    Page<User> findDoctorsBySpecialization(@Param("specialization") String specialization, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    Long countByIsActiveTrue();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    Long countByRoleAndIsActiveTrue(@Param("role") String role);
}
