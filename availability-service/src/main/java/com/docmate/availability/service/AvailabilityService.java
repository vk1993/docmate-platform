package com.docmate.availability.service;

import com.docmate.common.dto.AvailabilityDto;
import com.docmate.common.dto.CreateAvailabilityRequest;
import com.docmate.availability.repository.DoctorRecurringAvailabilityRepository;
import com.docmate.availability.repository.DoctorAdhocAvailabilityRepository;
import com.docmate.common.entity.DoctorAvailability;
import com.docmate.common.entity.TimeSlot;
import com.docmate.common.enums.AvailabilityStatus;
import com.docmate.common.enums.SlotStatus;
import com.docmate.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AvailabilityService {

    private final DoctorRecurringAvailabilityRepository recurringRepository;
    private final DoctorAdhocAvailabilityRepository adhocRepository;

    public AvailabilityDto setRecurringAvailability(CreateAvailabilityRequest request, UUID doctorId) {
        log.info("Setting recurring availability for doctor: {}", doctorId);

        if (!request.getIsRecurring() || request.getDayOfWeek() == null) {
            throw new BusinessException("INVALID_REQUEST", "Day of week is required for recurring availability", 400);
        }

        DoctorAvailability availability = DoctorAvailability.builder()
                .doctorId(doctorId)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isRecurring(true)
                .status(AvailabilityStatus.AVAILABLE)
                .build();

        availability = recurringRepository.save(availability);

        return mapToDto(availability);
    }

    public AvailabilityDto setAdhocAvailability(CreateAvailabilityRequest request, UUID doctorId) {
        log.info("Setting adhoc availability for doctor: {}", doctorId);

        if (request.getDate() == null) {
            throw new BusinessException("INVALID_REQUEST", "Date is required for adhoc availability", 400);
        }

        TimeSlot timeSlot = TimeSlot.builder()
                .doctorId(doctorId)
                .startTime(request.getDate().atTime(request.getStartTime()))
                .endTime(request.getDate().atTime(request.getEndTime()))
                .status(SlotStatus.AVAILABLE)
                .build();

        timeSlot = adhocRepository.save(timeSlot);

        return mapToDto(timeSlot);
    }

    @Transactional(readOnly = true)
    public List<AvailabilityDto> getDoctorAvailability(UUID doctorId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching availability for doctor: {} from {} to {}", doctorId, startDate, endDate);

        List<AvailabilityDto> availability = new ArrayList<>();

        // Add recurring availability
        List<DoctorAvailability> recurringSlots = recurringRepository.findByDoctorIdAndIsRecurringTrue(doctorId);
        availability.addAll(recurringSlots.stream().map(this::mapToDto).collect(Collectors.toList()));

        // Add adhoc availability for the date range
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        List<TimeSlot> adhocSlots = adhocRepository.findByDoctorIdAndTimeRange(doctorId, startDateTime, endDateTime);
        availability.addAll(adhocSlots.stream().map(this::mapToDto).collect(Collectors.toList()));

        return availability;
    }

    @Transactional(readOnly = true)
    public List<AvailabilityDto> getDoctorAvailabilityByDay(UUID doctorId, LocalDate date) {
        log.info("Fetching availability for doctor: {} on {}", doctorId, date);

        List<AvailabilityDto> availability = new ArrayList<>();

        // Add recurring availability for the day
        List<DoctorAvailability> recurringSlots = recurringRepository.findByDoctorIdAndIsRecurringTrue(doctorId);
        availability.addAll(recurringSlots.stream().map(this::mapToDto).collect(Collectors.toList()));

        // Add adhoc availability for the specific date
        List<TimeSlot> adhocSlots = adhocRepository.findByDoctorIdAndDateAndStatus(doctorId, date, SlotStatus.AVAILABLE);
        availability.addAll(adhocSlots.stream().map(this::mapToDto).collect(Collectors.toList()));

        return availability;
    }

    public boolean isDoctorAvailable(UUID doctorId, LocalDateTime appointmentDateTime, Integer durationMinutes) {
        LocalDate date = appointmentDateTime.toLocalDate();
        LocalTime time = appointmentDateTime.toLocalTime();

        // Check recurring availability
        List<DoctorAvailability> recurringAvailability = recurringRepository.findByDoctorIdAndIsRecurringTrue(doctorId);
        for (DoctorAvailability availability : recurringAvailability) {
            if (availability.getDayOfWeek().name().equals(date.getDayOfWeek().name()) &&
                !time.isBefore(availability.getStartTime()) &&
                !time.plusMinutes(durationMinutes).isAfter(availability.getEndTime())) {
                return true;
            }
        }

        // Check adhoc availability
        List<TimeSlot> adhocSlots = adhocRepository.findByDoctorIdAndDateAndStatus(doctorId, date, SlotStatus.AVAILABLE);
        for (TimeSlot slot : adhocSlots) {
            LocalTime slotStart = slot.getStartTime().toLocalTime();
            LocalTime slotEnd = slot.getEndTime().toLocalTime();
            if (!time.isBefore(slotStart) && !time.plusMinutes(durationMinutes).isAfter(slotEnd)) {
                return true;
            }
        }

        return false;
    }

    public List<AvailabilityDto> getAvailableSlots(UUID doctorId, LocalDate date) {
        log.info("Fetching available slots for doctor: {} on date: {}", doctorId, date);
        return getDoctorAvailabilityByDay(doctorId, date);
    }

    public void deleteAvailability(UUID availabilityId, UUID doctorId) {
        log.info("Deleting availability: {} for doctor: {}", availabilityId, doctorId);

        // Check if it's a recurring availability
        if (recurringRepository.existsById(availabilityId)) {
            DoctorAvailability availability = recurringRepository.findById(availabilityId)
                .orElseThrow(() -> new BusinessException("AVAILABILITY_NOT_FOUND", "Availability not found", 404));

            if (!availability.getDoctorId().equals(doctorId)) {
                throw new BusinessException("UNAUTHORIZED", "Cannot delete another doctor's availability", 403);
            }

            recurringRepository.deleteById(availabilityId);
            log.info("Deleted recurring availability: {}", availabilityId);
            return;
        }

        // Check if it's an adhoc availability (time slot)
        if (adhocRepository.existsById(availabilityId)) {
            TimeSlot timeSlot = adhocRepository.findById(availabilityId)
                .orElseThrow(() -> new BusinessException("AVAILABILITY_NOT_FOUND", "Time slot not found", 404));

            if (!timeSlot.getDoctorId().equals(doctorId)) {
                throw new BusinessException("UNAUTHORIZED", "Cannot delete another doctor's time slot", 403);
            }

            adhocRepository.deleteById(availabilityId);
            log.info("Deleted adhoc time slot: {}", availabilityId);
            return;
        }

        throw new BusinessException("AVAILABILITY_NOT_FOUND", "Availability not found", 404);
    }

    private AvailabilityDto mapToDto(DoctorAvailability availability) {
        return AvailabilityDto.builder()
                .id(availability.getId())
                .doctorId(availability.getDoctorId())
                .dayOfWeek(availability.getDayOfWeek())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .capacity(availability.getCapacity())
                .isActive(availability.getStatus() == AvailabilityStatus.AVAILABLE)
                .isRecurring(availability.getIsRecurring())
                .createdDate(availability.getCreatedDate())
                .build();
    }

    private AvailabilityDto mapToDto(TimeSlot timeSlot) {
        return AvailabilityDto.builder()
                .id(timeSlot.getId())
                .doctorId(timeSlot.getDoctorId())
                .date(timeSlot.getStartTime().toLocalDate())
                .startTime(timeSlot.getStartTime().toLocalTime())
                .endTime(timeSlot.getEndTime().toLocalTime())
                .capacity(1) // Default capacity for time slots
                .isActive(timeSlot.getStatus() == SlotStatus.AVAILABLE)
                .isRecurring(false)
                .createdDate(timeSlot.getCreatedDate())
                .build();
    }
}
