package com.docmate.user.service;

import com.docmate.common.entity.User;
import com.docmate.common.enums.ConsultationMode;
import com.docmate.common.exception.BusinessException;
import com.docmate.common.dto.UserDto;
import com.docmate.common.repository.UserRepository;
import com.docmate.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto getUserById(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found with ID: " + userId, 404));
        return userMapper.toDto(user);
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found with email: " + email, 404));
        return userMapper.toDto(user);
    }

    public Page<UserDto> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toDto);
    }

    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.email())) {
            throw new BusinessException("USER_EXISTS", "User already exists with email: " + userDto.email(), 409);
        }

        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        log.info("Created new user with ID: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    public UserDto updateUser(UUID userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found with ID: " + userId, 404));

        // Update fields
        userMapper.updateEntityFromDto(userDto, existingUser);
        User updatedUser = userRepository.save(existingUser);
        log.info("Updated user with ID: {}", userId);
        return userMapper.toDto(updatedUser);
    }

    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException("USER_NOT_FOUND", "User not found with ID: " + userId, 404);
        }

        userRepository.deleteById(userId);
        log.info("Deleted user with ID: {}", userId);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Page<UserDto> searchUsers(String searchTerm, Pageable pageable) {
        Page<User> users = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                searchTerm, searchTerm, searchTerm, pageable);
        return users.map(userMapper::toDto);
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found with email: " + email, 404));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BusinessException("INVALID_PASSWORD", "Current password is incorrect", 400);
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", email);
    }

    public Page<UserDto> getAllPatients(Pageable pageable) {
        Page<User> patients = userRepository.findByRole("PATIENT", pageable);
        return patients.map(userMapper::toDto);
    }

    public UserDto getUserProfile(String email) {
        return getUserByEmail(email);
    }

    public UserDto updateDoctorProfile(String email, UserDto userDto) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found with email: " + email, 404));

        userMapper.updateEntityFromDto(userDto, existingUser);
        User updatedUser = userRepository.save(existingUser);
        log.info("Updated doctor profile for email: {}", email);
        return userMapper.toDto(updatedUser);
    }

    public Page<UserDto> getAllDoctors(Pageable pageable) {
        Page<User> doctors = userRepository.findByRole("DOCTOR", pageable);
        return doctors.map(userMapper::toDto);
    }

    public UserDto getDoctorProfile(UUID doctorId) {
        return getUserById(doctorId);
    }

    public Page<UserDto> searchDoctorsWithFilters(String query, String specialization, String condition,
                                                 BigDecimal maxFee, ConsultationMode consultationMode,
                                                 Boolean emergencyAvailable, Pageable pageable) {
        // This would typically involve a complex query with joins to doctor table
        // For now, implementing basic search
        Page<User> doctors = userRepository.findDoctorsWithFilters(
                query, specialization, condition, maxFee, consultationMode, emergencyAvailable, pageable);
        return doctors.map(userMapper::toDto);
    }

    public Page<UserDto> getDoctorsBySpecialization(String specialization, Pageable pageable) {
        Page<User> doctors = userRepository.findDoctorsBySpecialization(specialization, pageable);
        return doctors.map(userMapper::toDto);
    }

    public Long getTotalActiveUsers() {
        return userRepository.countByIsActiveTrue();
    }

    public Long getTotalActivePatients() {
        return userRepository.countByRoleAndIsActiveTrue("PATIENT");
    }

    public Long getTotalActiveDoctors() {
        return userRepository.countByRoleAndIsActiveTrue("DOCTOR");
    }
}
