package com.docmate.user.mapper;

import com.docmate.common.entity.Specialization;
import com.docmate.common.dto.SpecializationDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpecializationMapper {

    SpecializationDto toDto(Specialization specialization);

    Specialization toEntity(SpecializationDto specializationDto);
}
