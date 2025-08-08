package com.docmate.auth.service;

import com.docmate.common.dto.user.UserDto;
import com.docmate.common.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMappingService {

    // Map from User entity to UserDto, automatically excluding passwordHash since it's not in UserDto
    UserDto toDto(User user);

    // Map from UserDto to User entity, passwordHash will need to be set separately
    User toEntity(UserDto userDto);
}
