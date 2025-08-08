package com.docmate.availability.repository;

import com.docmate.common.entity.TimeSlot;
import com.docmate.common.enums.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorAdhocAvailabilityRepository extends JpaRepository<TimeSlot, UUID> {

    List<TimeSlot> findByDoctorIdAndStatus(UUID doctorId, SlotStatus status);

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.doctorId = :doctorId AND DATE(ts.startTime) = :date AND ts.status = :status")
    List<TimeSlot> findByDoctorIdAndDateAndStatus(@Param("doctorId") UUID doctorId,
                                                  @Param("date") LocalDate date,
                                                  @Param("status") SlotStatus status);

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.doctorId = :doctorId AND ts.startTime BETWEEN :startTime AND :endTime")
    List<TimeSlot> findByDoctorIdAndTimeRange(@Param("doctorId") UUID doctorId,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.doctorId = :doctorId AND ts.startTime >= :fromTime AND ts.status = 'AVAILABLE'")
    List<TimeSlot> findAvailableSlotsByDoctorFromTime(@Param("doctorId") UUID doctorId,
                                                      @Param("fromTime") LocalDateTime fromTime);

    List<TimeSlot> findByDoctorIdOrderByStartTime(UUID doctorId);

    @Query("SELECT COUNT(ts) FROM TimeSlot ts WHERE ts.doctorId = :doctorId AND ts.status = :status")
    long countByDoctorIdAndStatus(@Param("doctorId") UUID doctorId, @Param("status") SlotStatus status);
}
