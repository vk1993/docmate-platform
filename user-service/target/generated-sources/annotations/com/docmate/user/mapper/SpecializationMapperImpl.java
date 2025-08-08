package com.docmate.user.mapper;

import com.docmate.common.dto.SpecializationDto;
import com.docmate.common.entity.Specialization;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-08T21:59:52+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class SpecializationMapperImpl implements SpecializationMapper {

    @Override
    public SpecializationDto toDto(Specialization specialization) {
        if ( specialization == null ) {
            return null;
        }

        SpecializationDto.SpecializationDtoBuilder specializationDto = SpecializationDto.builder();

        specializationDto.id( specialization.getId() );
        specializationDto.name( specialization.getName() );
        specializationDto.description( specialization.getDescription() );
        specializationDto.isActive( specialization.getIsActive() );
        specializationDto.createdDate( specialization.getCreatedDate() );

        return specializationDto.build();
    }

    @Override
    public Specialization toEntity(SpecializationDto specializationDto) {
        if ( specializationDto == null ) {
            return null;
        }

        Specialization.SpecializationBuilder specialization = Specialization.builder();

        specialization.name( specializationDto.getName() );
        specialization.description( specializationDto.getDescription() );
        specialization.isActive( specializationDto.getIsActive() );

        return specialization.build();
    }
}
