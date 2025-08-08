package com.docmate.user.mapper;

import com.docmate.common.entity.Doctor;
import com.docmate.common.dto.DoctorDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {UserMapper.class, SpecializationMapper.class, ConditionMapper.class, AddressMapper.class})
public interface DoctorMapper {

    @Mapping(target = "user", source = "user")
    @Mapping(target = "specialization", source = "specialization")
    @Mapping(target = "primaryAddress", source = "primaryAddress")
    @Mapping(target = "specializations", source = "specializations")
    @Mapping(target = "conditions", source = "conditions")
    DoctorDto toDto(Doctor doctor);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "specialization", ignore = true)
    @Mapping(target = "primaryAddress", ignore = true)
    @Mapping(target = "specializations", ignore = true)
    @Mapping(target = "conditions", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    Doctor toEntity(DoctorDto doctorDto);
}
