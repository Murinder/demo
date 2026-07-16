package com.example.ratingservice.controller;

import com.example.ratingservice.dto.*;
import com.example.ratingservice.service.StudentRatingService;
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
@RequestMapping("/api/v1/ratings/students")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Student Ratings", description = "Student rating APIs")
public class StudentRatingController {

    private final StudentRatingService studentRatingService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get student rating")
    public ResponseEntity<ApiResponse<StudentRatingDto>> getStudentRating(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(studentRatingService.getStudentRating(userId)));
    }

    @GetMapping("/top")
    @Operation(summary = "Get top students")
    public ResponseEntity<ApiResponse<List<StudentRatingDto>>> getTopStudents(
            @RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(ApiResponse.success(studentRatingService.getTopStudents(limit)));
    }

    @GetMapping("/{userId}/details")
    @Operation(summary = "Get rating history for a student (paginated)")
    public ResponseEntity<ApiResponse<List<RatingHistoryDto>>> getRatingDetails(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(studentRatingService.getRatingDetails(userId, page, size)));
    }

    @GetMapping("/{userId}/breakdown")
    @Operation(summary = "Get rating breakdown by category for a student")
    public ResponseEntity<ApiResponse<RatingBreakdownDto>> getBreakdown(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(studentRatingService.getBreakdown(userId)));
    }

    @GetMapping("/{userId}/comparison")
    @Operation(summary = "Get student rating compared to platform average")
    public ResponseEntity<ApiResponse<RatingComparisonDto>> getComparison(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(studentRatingService.getComparison(userId)));
    }

    @GetMapping("/{userId}/leaderboard")
    @Operation(summary = "Get top students leaderboard with user's rank")
    public ResponseEntity<ApiResponse<LeaderboardDto>> getLeaderboard(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success(studentRatingService.getLeaderboard(userId, limit)));
    }

    @GetMapping("/{userId}/achievements")
    @Operation(summary = "Get rating achievements (history) for a student")
    public ResponseEntity<ApiResponse<List<RatingAchievementDto>>> getAchievements(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(studentRatingService.getAchievements(userId)));
    }
}
