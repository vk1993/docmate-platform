package com.docmate.user.mapper;

import com.docmate.common.entity.Condition;
import com.docmate.common.dto.ConditionDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ConditionMapper {

    ConditionDto toDto(Condition condition);

    Condition toEntity(ConditionDto conditionDto);
}
