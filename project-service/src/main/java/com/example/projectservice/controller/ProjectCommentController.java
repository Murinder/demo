package com.example.projectservice.controller;

import com.example.projectservice.dto.ProjectCommentDto;
import com.example.projectservice.service.ProjectCommentService;
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
@RequestMapping("/api/v1/projects/{projectId}/comments")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Project Comments", description = "Комментарии к проектам")
public class ProjectCommentController {

    private final ProjectCommentService commentService;

    @GetMapping
    @Operation(summary = "Получить комментарии проекта")
    public ResponseEntity<ApiResponse<List<ProjectCommentDto>>> getComments(
            @PathVariable UUID projectId) {
        log.info("Getting comments for project {}", projectId);
        List<ProjectCommentDto> comments = commentService.getComments(projectId);
        return ResponseEntity.ok(
                ApiResponse.<List<ProjectCommentDto>>builder()
                        .success(true)
                        .code("COMMENTS_FETCHED")
                        .message("Comments fetched successfully")
                        .data(comments)
                        .build());
    }

    @PostMapping
    @Operation(summary = "Добавить комментарий к проекту")
    public ResponseEntity<ApiResponse<ProjectCommentDto>> addComment(
            @PathVariable UUID projectId,
            @RequestBody Map<String, String> body) {
        UUID authorId = UUID.fromString(body.get("authorId"));
        String authorRole = body.getOrDefault("authorRole", "STUDENT");
        String content = body.get("content");

        log.info("Adding comment to project {} by user {}", projectId, authorId);
        ProjectCommentDto comment = commentService.addComment(projectId, authorId, authorRole, content);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ProjectCommentDto>builder()
                        .success(true)
                        .code("COMMENT_CREATED")
                        .message("Comment added successfully")
                        .data(comment)
                        .build());
    }
}
