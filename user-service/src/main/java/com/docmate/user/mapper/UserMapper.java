package com.docmate.user.mapper;

import com.docmate.common.dto.UserDto;
import com.docmate.common.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    // Map from User entity to UserDto (don't expose passwordHash)
    @Mapping(target = "createdAt", source = "createdDate")
    @Mapping(target = "updatedAt", source = "updatedDate")
    UserDto toDto(User user);

    // Map from UserDto to User entity (ignore system-managed fields)
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(UserDto userDto);

    // Update existing User entity from UserDto (ignore system-managed fields)
    @Mapping(target = "passwordHash", ignore = true)
    void updateEntityFromDto(UserDto userDto, @MappingTarget User user);
}
