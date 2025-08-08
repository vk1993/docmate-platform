package com.docmate.auth.service;

import com.docmate.common.dto.user.UserDto;
import com.docmate.common.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-08T21:59:51+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class UserMappingServiceImpl implements UserMappingService {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.id( user.getId() );
        userDto.fullName( user.getFullName() );
        userDto.email( user.getEmail() );
        userDto.phone( user.getPhone() );
        userDto.role( user.getRole() );
        userDto.isActive( user.getIsActive() );
        userDto.profilePicture( user.getProfilePicture() );
        userDto.emailVerified( user.getEmailVerified() );
        userDto.phoneVerified( user.getPhoneVerified() );
        userDto.createdDate( user.getCreatedDate() );
        userDto.updatedDate( user.getUpdatedDate() );

        return userDto.build();
    }

    @Override
    public User toEntity(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.fullName( userDto.getFullName() );
        user.email( userDto.getEmail() );
        user.phone( userDto.getPhone() );
        user.role( userDto.getRole() );
        user.isActive( userDto.getIsActive() );
        user.profilePicture( userDto.getProfilePicture() );
        user.emailVerified( userDto.getEmailVerified() );
        user.phoneVerified( userDto.getPhoneVerified() );

        return user.build();
    }
}
