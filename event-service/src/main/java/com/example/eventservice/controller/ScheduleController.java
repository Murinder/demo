package com.example.eventservice.controller;

import com.example.eventservice.dto.CreateLessonDto;
import com.example.eventservice.dto.LessonDto;
import com.example.eventservice.service.LessonService;
import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events/schedule/lessons")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Schedule", description = "Lesson schedule APIs")
public class ScheduleController {

    private final LessonService lessonService;

    @GetMapping
    @Operation(summary = "Get lessons for a teacher")
    public ResponseEntity<ApiResponse<List<LessonDto>>> getLessons(
            @RequestParam UUID userId,
            @RequestParam(required = false) Integer semester) {
        List<LessonDto> lessons = lessonService.getLessonsByUser(userId, semester);
        return ResponseEntity.ok(ApiResponse.success(lessons));
    }

    @PostMapping
    @Operation(summary = "Create a lesson")
    public ResponseEntity<ApiResponse<LessonDto>> createLesson(@RequestBody CreateLessonDto dto) {
        LessonDto lesson = lessonService.createLesson(dto);
        return ResponseEntity.ok(ApiResponse.success(lesson, "Lesson created"));
    }

    @PutMapping("/{lessonId}")
    @Operation(summary = "Update a lesson")
    public ResponseEntity<ApiResponse<LessonDto>> updateLesson(
            @PathVariable UUID lessonId,
            @RequestBody CreateLessonDto dto) {
        LessonDto lesson = lessonService.updateLesson(lessonId, dto);
        return ResponseEntity.ok(ApiResponse.success(lesson, "Lesson updated"));
    }

    @DeleteMapping("/{lessonId}")
    @Operation(summary = "Delete a lesson")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(@PathVariable UUID lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.ok(ApiResponse.success(null, "Lesson deleted"));
    }
}
