package com.example.coreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Frontend-facing DTO matching the React RequestItem type.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationViewDto {
    private String id;
    private String title;
    private String description;
    private String submittedAt;      // formatted: "12.11.2025"
    private String status;           // PENDING, APPROVED, REJECTED, REVISION, ADMIN_REVIEW, IN_PROGRESS, COMPLETED, WITHDRAWN
    private String priority;         // HIGH, MEDIUM, LOW
    private String kind;             // PROJECT, CONSULT, VKR, RESOURCE_REQUEST, EQUIPMENT_REQUEST, ROOM_REQUEST, OTHER
    private StudentInfoDto student;
    private String category;
    private String duration;
    private String teamSize;
    private String teacherReply;
    private Integer applicationNumber;
    private String adminId;
    private String lecturerName;
    private List<ApplicationCommentDto> comments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentInfoDto {
        private String name;
        private String email;
        private String phone;
        private String group;
        private String course;
        private String initials;
    }
}
