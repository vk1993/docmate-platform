package com.docmate.auth.service;

import com.docmate.auth.repository.UserRepository;
import com.docmate.common.dto.auth.AuthResponse;
import com.docmate.common.dto.auth.LoginRequest;
import com.docmate.common.dto.auth.RegisterRequest;
import com.docmate.common.dto.auth.ForgotPasswordRequest;
import com.docmate.common.dto.auth.ResetPasswordRequest;
import com.docmate.common.dto.RegisterPatientRequest;
import com.docmate.common.dto.RegisterDoctorRequest;
import com.docmate.common.dto.UserProfileResponse;
import com.docmate.common.dto.user.UserDto;
import com.docmate.common.entity.User;
import com.docmate.common.entity.Patient;
import com.docmate.common.entity.Doctor;
import com.docmate.common.enums.UserRole;
import com.docmate.common.enums.Gender;
import com.docmate.common.exception.BusinessException;
import com.docmate.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMappingService userMappingService;

    public AuthResponse login(LoginRequest request) {
        log.info("Attempting login for email: {}", request.getEmail());

        User user = userRepository.findByEmailAndIsActiveTrue(request.getEmail())
                .orElseThrow(() -> new BusinessException("INVALID_CREDENTIALS", "Invalid email or password", 401));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("INVALID_CREDENTIALS", "Invalid email or password", 401);
        }

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getId());

        UserDto userDto = userMappingService.toDto(user);

        log.info("Login successful for user: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L) // 24 hours
                .user(userDto)
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting registration for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("EMAIL_ALREADY_EXISTS", "Email is already registered", 409);
        }

        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("PHONE_ALREADY_EXISTS", "Phone number is already registered", 409);
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isActive(true)
                .emailVerified(false)
                .phoneVerified(false)
                .build();

        user = userRepository.save(user);

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getId());

        UserDto userDto = userMappingService.toDto(user);

        log.info("Registration successful for user: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(userDto)
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        try {
            String email = jwtUtil.getEmailFromToken(refreshToken);
            UUID userId = jwtUtil.getUserIdFromToken(refreshToken);

            User user = userRepository.findByEmailAndIsActiveTrue(email)
                    .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found", 404));

            if (!jwtUtil.validateToken(refreshToken, email)) {
                throw new BusinessException("INVALID_TOKEN", "Invalid refresh token", 401);
            }

            String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getId());

            UserDto userDto = userMappingService.toDto(user);

            return AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(86400L)
                    .user(userDto)
                    .build();

        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            throw new BusinessException("INVALID_TOKEN", "Invalid refresh token", 401);
        }
    }

    public void logout(String accessToken) {
        // In a production environment, you would add the token to a blacklist
        // For now, we'll just log the logout
        try {
            String email = jwtUtil.getEmailFromToken(accessToken);
            log.info("User logged out: {}", email);
        } catch (Exception e) {
            log.warn("Invalid token during logout: {}", e.getMessage());
        }
    }

    public AuthResponse registerPatient(RegisterPatientRequest request) {
        log.info("Attempting patient registration for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("EMAIL_ALREADY_EXISTS", "Email is already registered", 409);
        }

        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("PHONE_ALREADY_EXISTS", "Phone number is already registered", 409);
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.PATIENT)
                .isActive(true)
                .emailVerified(false)
                .phoneVerified(false)
                .build();

        user = userRepository.save(user);

        // Create patient profile
        Patient patient = Patient.builder()
                .id(user.getId())
                .user(user)
                .dateOfBirth(request.getDateOfBirth() != null ? LocalDate.parse(request.getDateOfBirth()) : null)
                .gender(request.getGender() != null ? Gender.valueOf(request.getGender().toUpperCase()) : null)
                .bloodType(request.getBloodType())
                .height(request.getHeight())
                .weight(request.getWeight())
                .build();

        // Note: Patient would be saved via PatientRepository in a real implementation

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getId());

        UserDto userDto = userMappingService.toDto(user);

        log.info("Patient registration successful for user: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(userDto)
                .build();
    }

    public AuthResponse registerDoctor(RegisterDoctorRequest request) {
        log.info("Attempting doctor registration for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("EMAIL_ALREADY_EXISTS", "Email is already registered", 409);
        }

        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("PHONE_ALREADY_EXISTS", "Phone number is already registered", 409);
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.DOCTOR)
                .isActive(true)
                .emailVerified(false)
                .phoneVerified(false)
                .build();

        user = userRepository.save(user);

        // Create doctor profile (requires approval)
        Doctor doctor = Doctor.builder()
                .id(user.getId())
                .user(user)
                .licenseNumber(request.getLicenseNumber())
                .experienceYears(request.getExperienceYears() != null ? request.getExperienceYears() : 0)
                .feePerConsultation(request.getFeePerConsultation())
                .bio(request.getBio())
                .clinicName(request.getClinicName())
                .videoConsultationEnabled(request.getVideoConsultationEnabled() != null ? request.getVideoConsultationEnabled() : false)
                .teleConsultationEnabled(request.getTeleConsultationEnabled() != null ? request.getTeleConsultationEnabled() : false)
                .isApproved(false) // Requires admin approval
                .isActive(true)
                .build();

        // Note: Doctor would be saved via DoctorRepository in a real implementation

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getId());

        UserDto userDto = userMappingService.toDto(user);

        log.info("Doctor registration successful for user: {} (pending approval)", user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(userDto)
                .build();
    }

    public UserProfileResponse getCurrentUser(String email) {
        log.info("Getting current user profile for: {}", email);

        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found", 404));

        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .profilePicture(user.getProfilePicture())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .build();
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        log.info("Password reset requested for email: {}", request.getEmail());

        User user = userRepository.findByEmailAndIsActiveTrue(request.getEmail())
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found", 404));

        // Generate password reset token
        String resetToken = UUID.randomUUID().toString();

        // In a real implementation, you would:
        // 1. Store the reset token in database with expiration
        // 2. Send email with reset link containing the token

        log.info("Password reset email would be sent to: {}", request.getEmail());
        // TODO: Implement email service integration
    }

    public void resetPassword(ResetPasswordRequest request) {
        log.info("Password reset attempt with token: {}", request.getResetToken());

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("PASSWORD_MISMATCH", "Password confirmation does not match", 400);
        }

        // In a real implementation, you would:
        // 1. Validate the reset token from database
        // 2. Check if token is not expired
        // 3. Find user by token and update password

        // For now, we'll throw an exception to indicate the feature needs full implementation
        throw new BusinessException("NOT_IMPLEMENTED", "Password reset feature requires email service integration", 501);
    }
}
