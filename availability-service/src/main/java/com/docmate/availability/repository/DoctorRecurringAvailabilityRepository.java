package com.docmate.availability.repository;

import com.docmate.common.entity.DoctorAvailability;
import com.docmate.common.enums.AvailabilityStatus;
import com.docmate.common.enums.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorRecurringAvailabilityRepository extends JpaRepository<DoctorAvailability, UUID> {

    List<DoctorAvailability> findByDoctorIdAndStatus(UUID doctorId, AvailabilityStatus status);

    List<DoctorAvailability> findByDoctorIdAndDayOfWeekAndStatus(UUID doctorId, DayOfWeek dayOfWeek, AvailabilityStatus status);

    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctorId = :doctorId AND da.isRecurring = true AND da.status = 'AVAILABLE'")
    List<DoctorAvailability> findRecurringAvailabilityByDoctor(@Param("doctorId") UUID doctorId);

    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctorId = :doctorId AND da.dayOfWeek = :dayOfWeek AND " +
           "(da.effectiveFrom IS NULL OR da.effectiveFrom <= :date) AND " +
           "(da.effectiveUntil IS NULL OR da.effectiveUntil >= :date) AND da.status = 'AVAILABLE'")
    List<DoctorAvailability> findAvailabilityForDate(@Param("doctorId") UUID doctorId,
                                                      @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                                      @Param("date") LocalDate date);

    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctorId = :doctorId AND da.isRecurring = true " +
           "AND da.dayOfWeek = :dayOfWeek AND da.status = 'AVAILABLE'")
    List<DoctorAvailability> findRecurringByDoctorAndDay(@Param("doctorId") UUID doctorId, @Param("dayOfWeek") DayOfWeek dayOfWeek);

    List<DoctorAvailability> findByDoctorIdAndIsRecurringTrue(UUID doctorId);

    List<DoctorAvailability> findByDoctorIdAndIsRecurringFalse(UUID doctorId);
}
