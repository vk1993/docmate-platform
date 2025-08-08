package com.docmate.user.mapper;

import com.docmate.common.dto.UserDto;
import com.docmate.common.entity.User;
import com.docmate.common.enums.UserRole;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-08T21:59:52+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;
        UUID id = null;
        String fullName = null;
        String email = null;
        String phone = null;
        String role = null;
        String profilePicture = null;
        Boolean isActive = null;
        Boolean emailVerified = null;

        createdAt = user.getCreatedDate();
        updatedAt = user.getUpdatedDate();
        id = user.getId();
        fullName = user.getFullName();
        email = user.getEmail();
        phone = user.getPhone();
        if ( user.getRole() != null ) {
            role = user.getRole().name();
        }
        profilePicture = user.getProfilePicture();
        isActive = user.getIsActive();
        emailVerified = user.getEmailVerified();

        UserDto userDto = new UserDto( id, fullName, email, phone, role, profilePicture, isActive, emailVerified, createdAt, updatedAt );

        return userDto;
    }

    @Override
    public User toEntity(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.fullName( userDto.fullName() );
        user.email( userDto.email() );
        user.phone( userDto.phone() );
        if ( userDto.role() != null ) {
            user.role( Enum.valueOf( UserRole.class, userDto.role() ) );
        }
        user.isActive( userDto.isActive() );
        user.profilePicture( userDto.profilePicture() );
        user.emailVerified( userDto.emailVerified() );

        return user.build();
    }

    @Override
    public void updateEntityFromDto(UserDto userDto, User user) {
        if ( userDto == null ) {
            return;
        }

        user.setId( userDto.id() );
        user.setFullName( userDto.fullName() );
        user.setEmail( userDto.email() );
        user.setPhone( userDto.phone() );
        if ( userDto.role() != null ) {
            user.setRole( Enum.valueOf( UserRole.class, userDto.role() ) );
        }
        else {
            user.setRole( null );
        }
        user.setIsActive( userDto.isActive() );
        user.setProfilePicture( userDto.profilePicture() );
        user.setEmailVerified( userDto.emailVerified() );
    }
}
