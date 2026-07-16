package com.example.coreservice.controller;

import com.example.coreservice.dto.ApplicationCommentDto;
import com.example.coreservice.dto.ApplicationViewDto;
import com.example.coreservice.dto.CreateApplicationDto;
import com.example.coreservice.service.ApplicationService;
import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Applications", description = "Student applications/requests to lecturers")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "Get applications for a lecturer")
    @GetMapping("/lecturer/{lecturerId}")
    public ResponseEntity<ApiResponse<List<ApplicationViewDto>>> getByLecturer(
            @PathVariable UUID lecturerId) {
        List<ApplicationViewDto> items = applicationService.getByLecturer(lecturerId);
        return ResponseEntity.ok(ApiResponse.<List<ApplicationViewDto>>builder()
                .success(true)
                .code("APPLICATIONS_RETRIEVED")
                .message("Applications retrieved successfully")
                .data(items)
                .build());
    }

    @Operation(summary = "Get applications by a student")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<ApplicationViewDto>>> getByStudent(
            @PathVariable UUID studentId) {
        List<ApplicationViewDto> items = applicationService.getByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.<List<ApplicationViewDto>>builder()
                .success(true)
                .code("APPLICATIONS_RETRIEVED")
                .message("Student applications retrieved successfully")
                .data(items)
                .build());
    }

    @Operation(summary = "Get applications pending admin (department head) review")
    @GetMapping("/admin/pending")
    public ResponseEntity<ApiResponse<List<ApplicationViewDto>>> getByAdmin() {
        List<ApplicationViewDto> items = applicationService.getByAdmin();
        return ResponseEntity.ok(ApiResponse.<List<ApplicationViewDto>>builder()
                .success(true)
                .code("APPLICATIONS_RETRIEVED")
                .message("Admin applications retrieved successfully")
                .data(items)
                .build());
    }

    @Operation(summary = "Create a new application")
    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationViewDto>> create(
            @RequestParam UUID studentId,
            @RequestBody CreateApplicationDto dto) {
        ApplicationViewDto created = applicationService.create(studentId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ApplicationViewDto>builder()
                        .success(true)
                        .code("APPLICATION_CREATED")
                        .message("Application created successfully")
                        .data(created)
                        .build());
    }

    @Operation(summary = "Update application status")
    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<ApiResponse<ApplicationViewDto>> updateStatus(
            @PathVariable UUID applicationId,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        String comment = body.get("teacherReply");
        if (comment == null) {
            comment = body.get("comment");
        }
        ApplicationViewDto updated = applicationService.updateStatus(applicationId, status, comment);
        return ResponseEntity.ok(ApiResponse.<ApplicationViewDto>builder()
                .success(true)
                .code("APPLICATION_STATUS_UPDATED")
                .message("Application status updated successfully")
                .data(updated)
                .build());
    }

    @Operation(summary = "Add a comment to an application")
    @PostMapping("/{applicationId}/comments")
    public ResponseEntity<ApiResponse<ApplicationViewDto>> addComment(
            @PathVariable UUID applicationId,
            @RequestBody Map<String, String> body) {
        UUID authorId = UUID.fromString(body.get("authorId"));
        String content = body.get("content");
        ApplicationViewDto updated = applicationService.addComment(applicationId, authorId, content);
        return ResponseEntity.ok(ApiResponse.<ApplicationViewDto>builder()
                .success(true)
                .code("COMMENT_ADDED")
                .message("Comment added successfully")
                .data(updated)
                .build());
    }

    @Operation(summary = "Get comments for an application")
    @GetMapping("/{applicationId}/comments")
    public ResponseEntity<ApiResponse<List<ApplicationCommentDto>>> getComments(
            @PathVariable UUID applicationId) {
        List<ApplicationCommentDto> comments = applicationService.getComments(applicationId);
        return ResponseEntity.ok(ApiResponse.<List<ApplicationCommentDto>>builder()
                .success(true)
                .code("COMMENTS_RETRIEVED")
                .message("Comments retrieved successfully")
                .data(comments)
                .build());
    }

    @Operation(summary = "Withdraw an application (student)")
    @PatchMapping("/{applicationId}/withdraw")
    public ResponseEntity<ApiResponse<ApplicationViewDto>> withdraw(
            @PathVariable UUID applicationId) {
        ApplicationViewDto updated = applicationService.withdraw(applicationId);
        return ResponseEntity.ok(ApiResponse.<ApplicationViewDto>builder()
                .success(true)
                .code("APPLICATION_WITHDRAWN")
                .message("Application withdrawn successfully")
                .data(updated)
                .build());
    }
}
