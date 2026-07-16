package com.example.sharedlib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for user profile
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String bio;
    private String avatarUrl;
    private String role;
    private boolean active;
    private OffsetDateTime lastLogin;
    private OffsetDateTime createdAt;
    private LocalDate birthDate;
    private UUID facultyId;
    private UUID departmentId;
    private String studyProgram;
    private String groupName;
    private Integer enrollmentYear;
    private Integer currentSemester;
    private String studyForm;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String interests;

    // Teacher-specific fields
    private String position;
    private String degree;
    private String teacherId;
    private String experience;
    private String office;
    private String officeHours;
    private String website;
    private String linkedin;

    // Head-specific academic fields
    private String academicTitle;
    private LocalDate headSince;
    private String dissertationTitle;
    private Integer dissertationYear;
    private String educationHistory;

    // Resolved names (from facultyId/departmentId)
    private String facultyName;
    private String departmentName;
}