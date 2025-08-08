package com.docmate.user.mapper;

import com.docmate.common.dto.ConditionDto;
import com.docmate.common.entity.Condition;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-08T21:59:52+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class ConditionMapperImpl implements ConditionMapper {

    @Override
    public ConditionDto toDto(Condition condition) {
        if ( condition == null ) {
            return null;
        }

        ConditionDto.ConditionDtoBuilder conditionDto = ConditionDto.builder();

        conditionDto.id( condition.getId() );
        conditionDto.name( condition.getName() );
        conditionDto.description( condition.getDescription() );
        conditionDto.isActive( condition.getIsActive() );
        conditionDto.createdDate( condition.getCreatedDate() );

        return conditionDto.build();
    }

    @Override
    public Condition toEntity(ConditionDto conditionDto) {
        if ( conditionDto == null ) {
            return null;
        }

        Condition.ConditionBuilder condition = Condition.builder();

        condition.name( conditionDto.getName() );
        condition.description( conditionDto.getDescription() );
        condition.isActive( conditionDto.getIsActive() );

        return condition.build();
    }
}
