package com.example.coreservice.model.entity;

import com.example.sharedlib.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "user_role")
    private UserRole role;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "last_login")
    private OffsetDateTime lastLogin;

    @Column
    private String phone;

    @Column
    private String address;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "faculty_id")
    private UUID facultyId;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "study_program")
    private String studyProgram;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "enrollment_year")
    private Integer enrollmentYear;

    @Column(name = "current_semester")
    private Integer currentSemester;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column
    private String bio;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "study_form")
    private String studyForm;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    @Column(columnDefinition = "TEXT")
    private String interests;

    @Column
    private String position;

    @Column
    private String degree;

    @Column(name = "teacher_id")
    private String teacherId;

    @Column
    private String experience;

    @Column
    private String office;

    @Column(name = "office_hours")
    private String officeHours;

    @Column
    private String website;

    @Column
    private String linkedin;

    @Column(name = "academic_title")
    private String academicTitle;

    @Column(name = "head_since")
    private LocalDate headSince;

    @Column(name = "dissertation_title", columnDefinition = "TEXT")
    private String dissertationTitle;

    @Column(name = "dissertation_year")
    private Integer dissertationYear;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "education_history", columnDefinition = "jsonb")
    private String educationHistory;
}