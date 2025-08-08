package com.docmate.appointment.mapper;

import com.docmate.appointment.dto.AppointmentDto;
import com.docmate.appointment.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppointmentMapper {

    @Mapping(target = "doctor", ignore = true) // Will be populated by service layer
    @Mapping(target = "patient", ignore = true) // Will be populated by service layer
    AppointmentDto toDto(Appointment appointment);

    Appointment toEntity(AppointmentDto appointmentDto);
}
