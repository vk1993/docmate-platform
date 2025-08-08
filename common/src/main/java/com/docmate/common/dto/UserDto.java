package com.docmate.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDto(
    UUID id,

    @NotBlank(message = "Full name is required")
    String fullName,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phone,

    String role,
    String profilePicture,
    Boolean isActive,
    Boolean emailVerified,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    // Factory method for creating a new user (without system-generated fields)
    public static UserDto createNew(
            String fullName,
            String email,
            String phone,
            String role) {
        return new UserDto(
                null, // id will be generated
                fullName,
                email,
                phone,
                role,
                null, // profilePicture
                true, // isActive
                false, // emailVerified
                null, // createdAt will be set by system
                null  // updatedAt will be set by system
        );
    }
}
