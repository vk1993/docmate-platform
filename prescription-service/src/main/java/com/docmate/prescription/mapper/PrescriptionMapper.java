package com.docmate.prescription.mapper;

import com.docmate.common.entity.Prescription;
import com.docmate.prescription.dto.PrescriptionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PrescriptionMapper {
    
    @Mapping(target = "appointmentId", source = "appointment.id")
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "patientId", source = "patient.id")
    PrescriptionDto toDto(Prescription prescription);
}
