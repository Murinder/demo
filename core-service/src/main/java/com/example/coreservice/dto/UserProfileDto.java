//package com.example.coreservice.dto;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.OffsetDateTime;
//import java.util.UUID;
//
///**
// * DTO for user profile
// */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public class UserProfileDto {
//
//    private UUID id;
//    private String email;
//    private String firstName;
//    private String lastName;
//    private String phoneNumber;
//    private String address;
//    private String bio;
//    private String avatarUrl;
//    private String role;
//    private boolean active;
//    private OffsetDateTime lastLogin;
//    private OffsetDateTime createdAt;
//    private LocalDate birthDate;
//    private UUID facultyId;
//    private UUID departmentId;
//    private String studyProgram;
//    private String groupName;
//    private Integer enrollmentYear;
//    private Integer currentSemester;
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public String getBio() {
//        return bio;
//    }
//}