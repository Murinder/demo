package com.example.coreservice.service;

import com.example.coreservice.dto.HeadProfileDto;
import com.example.coreservice.dto.TeacherProfileDto;
import com.example.coreservice.dto.UserDto;
import com.example.coreservice.dto.UserLoginDto;
import com.example.coreservice.dto.UserRegistrationDto;
import com.example.coreservice.model.entity.LecturerAcademicMetrics;
import com.example.coreservice.model.entity.User;
import com.example.coreservice.model.entity.UserAward;
import com.example.sharedlib.dto.UserProfileDto;
import com.example.sharedlib.enums.UserRole;
import com.example.coreservice.repository.DepartmentRepository;
import com.example.coreservice.repository.FacultyRepository;
import com.example.coreservice.repository.LecturerAcademicMetricsRepository;
import com.example.coreservice.repository.UserAwardRepository;
import com.example.coreservice.repository.UserRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import com.example.sharedlib.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service для управления пользователями
 * Включает функции регистрации, аутентификации, и управления профилями
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final LecturerAcademicMetricsRepository metricsRepository;
    private final UserAwardRepository userAwardRepository;
    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Получить расширенный профиль заведующего кафедрой
     */
    @Transactional(readOnly = true)
    public HeadProfileDto getHeadProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfileDto profile = mapToProfileDto(user);

        HeadProfileDto.HeadProfileDtoBuilder builder = HeadProfileDto.builder().profile(profile);

        metricsRepository.findById(userId).ifPresent(m -> {
            builder.publications(m.getPublications())
                    .monographs(m.getMonographs())
                    .articles(m.getArticles())
                    .conferences(m.getConferences())
                    .grants(m.getGrants())
                    .hours(m.getHours())
                    .consultations(m.getConsultations())
                    .teachingStartYear(m.getTeachingStartYear())
                    .supervisedPhd(m.getSupervisedPhd())
                    .supervisedMasters(m.getSupervisedMasters())
                    .supervisedBachelors(m.getSupervisedBachelors());
        });

        if (user.getDepartmentId() != null) {
            builder.departmentTeacherCount(
                    (int) userRepository.countByDepartmentIdAndRoleAndIsActiveTrue(user.getDepartmentId(), UserRole.LECTURER));
            builder.departmentStudentCount(
                    (int) userRepository.countByDepartmentIdAndRoleAndIsActiveTrue(user.getDepartmentId(), UserRole.STUDENT));
        }

        List<UserAward> awards = userAwardRepository.findByUserId(userId);
        builder.awards(awards.stream()
                .map(a -> HeadProfileDto.AwardItem.builder()
                        .id(a.getId().toString())
                        .title(a.getTitle())
                        .year(a.getYear())
                        .build())
                .toList());

        return builder.build();
    }

    /**
     * Получить расширенный профиль преподавателя
     */
    @Transactional(readOnly = true)
    public TeacherProfileDto getTeacherProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfileDto profile = mapToProfileDto(user);

        TeacherProfileDto.TeacherProfileDtoBuilder builder = TeacherProfileDto.builder().profile(profile);

        metricsRepository.findById(userId).ifPresent(m -> {
            builder.publications(m.getPublications())
                    .monographs(m.getMonographs())
                    .articles(m.getArticles())
                    .conferences(m.getConferences())
                    .grants(m.getGrants())
                    .hours(m.getHours())
                    .consultations(m.getConsultations())
                    .teachingStartYear(m.getTeachingStartYear())
                    .supervisedPhd(m.getSupervisedPhd())
                    .supervisedMasters(m.getSupervisedMasters())
                    .supervisedBachelors(m.getSupervisedBachelors());
        });

        List<UserAward> awards = userAwardRepository.findByUserId(userId);
        builder.awards(awards.stream()
                .map(a -> HeadProfileDto.AwardItem.builder()
                        .id(a.getId().toString())
                        .title(a.getTitle())
                        .year(a.getYear())
                        .build())
                .toList());

        return builder.build();
    }

    /**
     * Регистрация нового пользователя
     */
    public UserDto registerUser(UserRegistrationDto registrationDto) {
        log.info("Registering new user with email: {}", registrationDto.getEmail());

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new ValidationException("User with this email already exists");
        }

        User user = User.builder()
                .id(UUID.randomUUID())
                .email(registrationDto.getEmail())
                .passwordHash(passwordEncoder.encode(registrationDto.getPassword()))
                .role(UserRole.STUDENT)
                .isVerified(false)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getId());
        return mapToUserDto(savedUser);
    }

    /**
     * Получить пользователя по ID
     */
    @Cacheable(value = "users", key = "#id")
    @Transactional(readOnly = true)
    public UserProfileDto getUserById(UUID id) {
        User user = userRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found");
                });
        return mapToProfileDto(user);
    }

    /**
     * Получить пользователя по email
     */
    @Transactional(readOnly = true)
    public UserProfileDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found");
                });
        return mapToProfileDto(user);
    }

    /**
     * Обновить профиль пользователя
     */
    @CacheEvict(value = "users", key = "#id")
    public UserProfileDto updateUserProfile(UUID id, UserProfileDto updateDto) {
        log.info("Updating user profile: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (updateDto.getFirstName() != null) user.setFirstName(updateDto.getFirstName());
        if (updateDto.getLastName() != null) user.setLastName(updateDto.getLastName());
        if (updateDto.getPhoneNumber() != null) user.setPhone(updateDto.getPhoneNumber());
        if (updateDto.getAddress() != null) user.setAddress(updateDto.getAddress());
        if (updateDto.getBio() != null) user.setBio(updateDto.getBio());
        if (updateDto.getAvatarUrl() != null) user.setAvatarUrl(updateDto.getAvatarUrl());
        if (updateDto.getBirthDate() != null) user.setBirthDate(updateDto.getBirthDate());
        if (updateDto.getStudyProgram() != null) user.setStudyProgram(updateDto.getStudyProgram());
        if (updateDto.getGroupName() != null) user.setGroupName(updateDto.getGroupName());
        if (updateDto.getEnrollmentYear() != null) user.setEnrollmentYear(updateDto.getEnrollmentYear());
        if (updateDto.getCurrentSemester() != null) user.setCurrentSemester(updateDto.getCurrentSemester());
        if (updateDto.getFacultyId() != null) user.setFacultyId(updateDto.getFacultyId());
        if (updateDto.getDepartmentId() != null) user.setDepartmentId(updateDto.getDepartmentId());
        if (updateDto.getStudyForm() != null) user.setStudyForm(updateDto.getStudyForm());
        if (updateDto.getEmergencyContactName() != null) user.setEmergencyContactName(updateDto.getEmergencyContactName());
        if (updateDto.getEmergencyContactPhone() != null) user.setEmergencyContactPhone(updateDto.getEmergencyContactPhone());
        if (updateDto.getInterests() != null) user.setInterests(updateDto.getInterests());
        if (updateDto.getPosition() != null) user.setPosition(updateDto.getPosition());
        if (updateDto.getDegree() != null) user.setDegree(updateDto.getDegree());
        if (updateDto.getTeacherId() != null) user.setTeacherId(updateDto.getTeacherId());
        if (updateDto.getExperience() != null) user.setExperience(updateDto.getExperience());
        if (updateDto.getOffice() != null) user.setOffice(updateDto.getOffice());
        if (updateDto.getOfficeHours() != null) user.setOfficeHours(updateDto.getOfficeHours());
        if (updateDto.getWebsite() != null) user.setWebsite(updateDto.getWebsite());
        if (updateDto.getLinkedin() != null) user.setLinkedin(updateDto.getLinkedin());
        if (updateDto.getAcademicTitle() != null) user.setAcademicTitle(updateDto.getAcademicTitle());
        if (updateDto.getHeadSince() != null) user.setHeadSince(updateDto.getHeadSince());
        if (updateDto.getDissertationTitle() != null) user.setDissertationTitle(updateDto.getDissertationTitle());
        if (updateDto.getDissertationYear() != null) user.setDissertationYear(updateDto.getDissertationYear());
        if (updateDto.getEducationHistory() != null) user.setEducationHistory(updateDto.getEducationHistory());

        User updatedUser = userRepository.save(user);
        return mapToProfileDto(updatedUser);
    }

    /**
     * Получить всех пользователей по роли
     */
    @Transactional(readOnly = true)
    public List<UserProfileDto> getUsersByRole(UserRole role) {
        return userRepository.findByRoleAndIsActiveTrue(role).stream()
                .map(this::mapToProfileDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserProfileDto> searchUsersByName(String query) {
        return userRepository.searchByName(query.trim(), PageRequest.of(0, 15)).stream()
                .map(this::mapToProfileDto)
                .toList();
    }

    /**
     * Get student user IDs by group name.
     */
    @Transactional(readOnly = true)
    public List<UUID> getStudentIdsByGroup(String groupName) {
        return userRepository.findByGroupNameAndRoleAndIsActiveTrue(groupName, UserRole.STUDENT)
                .stream().map(User::getId).toList();
    }

    @Transactional(readOnly = true)
    public List<String> getAllGroupNames() {
        return userRepository.findDistinctGroupNames();
    }

    /**
     * Get student user IDs by department.
     */
    @Transactional(readOnly = true)
    public List<UUID> getStudentIdsByDepartment(UUID departmentId) {
        return userRepository.findByDepartmentIdAndRoleAndIsActiveTrue(departmentId, UserRole.STUDENT)
                .stream().map(User::getId).toList();
    }

    /**
     * Get student user IDs by faculty.
     */
    @Transactional(readOnly = true)
    public List<UUID> getStudentIdsByFaculty(UUID facultyId) {
        return userRepository.findByFacultyIdAndRoleAndIsActiveTrue(facultyId, UserRole.STUDENT)
                .stream().map(User::getId).toList();
    }

    /**
     * Получить пользователя для внутреннего использования
     */
    @Transactional(readOnly = true)
    public User getUserEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Маппить User к UserProfileDto
     */
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
                .role(user.getRole().toString())
                .active(user.getIsActive())
                .phoneNumber(user.getPhone())
                .address(user.getAddress())
                .birthDate(user.getBirthDate())
                .facultyId(user.getFacultyId())
                .departmentId(user.getDepartmentId())
                .studyProgram(user.getStudyProgram())
                .groupName(user.getGroupName())
                .enrollmentYear(user.getEnrollmentYear())
                .currentSemester(user.getCurrentSemester())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
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

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .isVerified(user.getIsVerified())
                .isActive(user.getIsActive())
                .phoneNumber(user.getPhone())
                .address(user.getAddress())
                .birthDate(user.getBirthDate())
                .facultyId(user.getFacultyId())
                .departmentId(user.getDepartmentId())
                .studyProgram(user.getStudyProgram())
                .groupName(user.getGroupName())
                .enrollmentYear(user.getEnrollmentYear())
                .currentSemester(user.getCurrentSemester())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
}