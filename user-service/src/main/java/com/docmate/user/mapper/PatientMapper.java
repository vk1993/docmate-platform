package com.docmate.user.mapper;

import com.docmate.common.dto.PatientProfileDto;
import com.docmate.common.entity.Patient;
import com.docmate.common.dto.PatientDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class, AddressMapper.class})
public interface PatientMapper {

    @Mapping(target = "user", source = "user")
    @Mapping(target = "address", source = "address")
    PatientDto toDto(Patient patient);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "id", ignore = true)
    Patient toEntity(PatientDto patientDto);

    // Simplified profile mapping - explicitly ignore id to prevent UUID conversion issues
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "phone", source = "user.phone")
    PatientProfileDto toProfileDto(Patient patient);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    void updateEntityFromDto(PatientDto patientDto, @MappingTarget Patient patient);
}
