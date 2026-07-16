package com.example.projectservice.controller;

import com.example.sharedlib.dto.ProjectMemberDto;
import com.example.projectservice.service.ProjectMemberService;
import com.example.sharedlib.enums.ProjectRole;
import com.example.sharedlib.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/members")
@RequiredArgsConstructor
@Tag(name = "Project Members", description = "Endpoints для управления участниками проекта")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;
    private final com.example.projectservice.service.ProjectService projectService;

    @PostMapping
    @PreAuthorize("@projectSecurity.isLeader(#projectId, authentication.name)")
    @Operation(summary = "Добавить участника в проект")
    public ResponseEntity<ApiResponse<ProjectMemberDto>> addMember(
            @PathVariable UUID projectId,
            @RequestParam UUID userId,
            @RequestParam ProjectRole role) {
        com.example.projectservice.model.Project project = projectService.getProjectEntityById(projectId);
        ProjectMemberDto newMember = projectMemberService.addMember(project, userId, role);
        return ResponseEntity.ok(ApiResponse.<ProjectMemberDto>builder()
                .success(true)
                .code("MEMBER_ADDED")
                .message("Member added successfully")
                .data(newMember)
                .build());
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("@projectSecurity.isLeader(#projectId, authentication.name)")
    @Operation(summary = "Удалить участника из проекта")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable UUID projectId,
            @PathVariable UUID userId) {
        projectMemberService.removeMember(projectId, userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .code("MEMBER_REMOVED")
                .message("Member removed successfully")
                .build());
    }

    @DeleteMapping("/leave")
    @Operation(summary = "Покинуть проект")
    public ResponseEntity<ApiResponse<Void>> leaveProject(@PathVariable UUID projectId, @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        projectMemberService.removeMember(projectId, userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .code("PROJECT_LEFT")
                .message("Successfully left the project")
                .build());
    }

    @GetMapping
    @Operation(summary = "Получить всех участников проекта")
    public ResponseEntity<ApiResponse<List<ProjectMemberDto>>> getProjectMembers(@PathVariable UUID projectId) {
        List<ProjectMemberDto> members = projectMemberService.getProjectMembers(projectId);
        return ResponseEntity.ok(ApiResponse.<List<ProjectMemberDto>>builder()
                .success(true)
                .code("MEMBERS_RETRIEVED")
                .message("Members retrieved successfully")
                .data(members)
                .build());
    }
}