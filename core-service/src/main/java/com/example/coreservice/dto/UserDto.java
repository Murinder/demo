package com.example.coreservice.dto;

import com.example.sharedlib.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO для User сущности
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String email;
    private UserRole role;
    private Boolean isVerified;
    private Boolean isActive;
    private String phoneNumber;
    private String address;
    private LocalDate birthDate;
    private UUID facultyId;
    private UUID departmentId;
    private String studyProgram;
    private String groupName;
    private Integer enrollmentYear;
    private Integer currentSemester;
    private String avatarUrl;
    private String bio;
    private OffsetDateTime createdAt;
    private OffsetDateTime lastLogin;
}