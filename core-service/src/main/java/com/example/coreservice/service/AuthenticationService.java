package com.example.coreservice.service;

import com.example.coreservice.dto.LoginRequest;
import com.example.coreservice.dto.RegisterRequest;
import com.example.sharedlib.security.JwtTokenProvider;
import com.example.sharedlib.dto.UserProfileDto;
import com.example.coreservice.model.entity.User;
import com.example.coreservice.repository.UserRepository;
import com.example.coreservice.repository.FacultyRepository;
import com.example.coreservice.repository.DepartmentRepository;
import com.example.sharedlib.dto.TokenDto;
import com.example.sharedlib.enums.UserRole;
import com.example.sharedlib.exception.AuthenticationException;
import com.example.sharedlib.exception.ResourceNotFoundException;
import com.example.sharedlib.exception.ValidationException;
import com.example.sharedlib.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.sharedlib.event.EventPublisher;
import com.example.sharedlib.event.UserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Service for authentication operations
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EventPublisher eventPublisher;
    private final EmailService emailService;
    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;

    /**
     * Register new user
     */
    public UserProfileDto register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        ValidationUtil.validateEmail(request.getEmail());
        ValidationUtil.validatePasswordStrength(request.getPassword());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("User with this email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhoneNumber())
                .role(UserRole.STUDENT)
                .isActive(true)
                .build();

        user = userRepository.save(user);

        UserEvent userEvent = UserEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .build();
        eventPublisher.publish("etsopy.user", "user.created", userEvent);

        return mapToProfileDto(user);
    }

    /**
     * Login user
     */
    public TokenDto login(LoginRequest request) {
        log.info("User login attempt with email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!user.getIsActive()) {
            throw new AuthenticationException("User account is inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid email or password");
        }

        user.setLastLogin(OffsetDateTime.now());
        userRepository.save(user);

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId().toString(), user.getEmail(), user.getRole().toString());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId().toString());

        log.info("User logged in successfully: {}", user.getEmail());

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(86400)
                .tokenType("Bearer")
                .build();
    }

    /**
     * Get user by ID
     */
    public UserProfileDto getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return mapToProfileDto(user);
    }

    /**
     * Refresh access token using a valid refresh token
     */
    public TokenDto refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AuthenticationException("Invalid or expired refresh token");
        }

        String tokenType = jwtTokenProvider.getClaimsFromToken(refreshToken).get("type", String.class);
        if (!"refresh".equals(tokenType)) {
            throw new AuthenticationException("Token is not a refresh token");
        }

        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!user.getIsActive()) {
            throw new AuthenticationException("User account is inactive");
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(
                user.getId().toString(), user.getEmail(), user.getRole().toString());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId().toString());

        return TokenDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(86400)
                .tokenType("Bearer")
                .build();
    }

    /**
     * Initiate password reset - generates a temporary token and sends email
     */
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String resetToken = jwtTokenProvider.generateAccessToken(
                user.getId().toString(), user.getEmail(), "RESET");

        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        log.info("Password reset initiated for user: {}", email);
    }

    /**
     * Reset password using a valid reset token
     */
    public void resetPassword(String resetToken, String newPassword) {
        if (!jwtTokenProvider.validateToken(resetToken)) {
            throw new AuthenticationException("Invalid or expired reset token");
        }

        ValidationUtil.validatePasswordStrength(newPassword);

        String userId = jwtTokenProvider.getUserIdFromToken(resetToken);
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password reset successfully for user: {}", user.getEmail());
    }

    private UserProfileDto mapToProfileDto(User user) {
        String facultyName = null;
        String departmentName = null;
        if (user.getFacultyId() != null) {
            facultyName = facultyRepository.findById(user.getFacultyId())
                    .map(f -> f.getName()).orElse(null);
        }
        if (user.getDepartmentId() != null) {
            departmentName = departmentRepository.findById(user.getDepartmentId())
                    .map(d -> d.getName()).orElse(null);
        }

        return UserProfileDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhone())
                .address(user.getAddress())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().toString())
                .active(user.getIsActive())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .birthDate(user.getBirthDate())
                .facultyId(user.getFacultyId())
                .departmentId(user.getDepartmentId())
                .studyProgram(user.getStudyProgram())
                .groupName(user.getGroupName())
                .enrollmentYear(user.getEnrollmentYear())
                .currentSemester(user.getCurrentSemester())
                .studyForm(user.getStudyForm())
                .emergencyContactName(user.getEmergencyContactName())
                .emergencyContactPhone(user.getEmergencyContactPhone())
                .interests(user.getInterests())
                .position(user.getPosition())
                .degree(user.getDegree())
                .teacherId(user.getTeacherId())
                .experience(user.getExperience())
                .office(user.getOffice())
                .officeHours(user.getOfficeHours())
                .website(user.getWebsite())
                .linkedin(user.getLinkedin())
                .academicTitle(user.getAcademicTitle())
                .headSince(user.getHeadSince())
                .dissertationTitle(user.getDissertationTitle())
                .dissertationYear(user.getDissertationYear())
                .educationHistory(user.getEducationHistory())
                .facultyName(facultyName)
                .departmentName(departmentName)
                .build();
    }
}