package com.example.portfolioservice.controller;

import com.example.portfolioservice.dto.*;
import com.example.portfolioservice.service.AchievementService;
import com.example.portfolioservice.service.PortfolioService;
import com.example.portfolioservice.service.PortfolioSkillService;
import com.example.portfolioservice.service.ReviewService;
import com.example.sharedlib.security.AuthenticatedOnly;
import com.example.sharedlib.security.LecturerOrAbove;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Portfolios", description = "Portfolio management APIs")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final AchievementService achievementService;
    private final PortfolioSkillService skillService;
    private final ReviewService reviewService;

    // --- Portfolio ---

    @GetMapping("/{userId}")
    @Operation(summary = "Get portfolio by user ID")
    public ResponseEntity<PortfolioDto> getPortfolio(@PathVariable UUID userId) {
        return ResponseEntity.ok(portfolioService.getPortfolioByUserId(userId));
    }

    @PutMapping("/{userId}/visibility")
    @Operation(summary = "Update portfolio visibility settings")
    public ResponseEntity<PortfolioDto> updateVisibility(@PathVariable UUID userId, @RequestBody String visibilitySettings) {
        return ResponseEntity.ok(portfolioService.updateVisibility(userId, visibilitySettings));
    }

    // --- Achievements ---

    @GetMapping("/{userId}/achievements")
    @Operation(summary = "Get user achievements")
    public ResponseEntity<List<AchievementDto>> getAchievements(@PathVariable UUID userId) {
        return ResponseEntity.ok(achievementService.getAchievementsByUserId(userId));
    }

    @PostMapping("/{userId}/achievements")
    @Operation(summary = "Add an achievement")
    public ResponseEntity<AchievementDto> addAchievement(@PathVariable UUID userId, @RequestBody CreateAchievementDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(achievementService.createAchievement(userId, dto));
    }

    @DeleteMapping("/achievements/{achievementId}")
    @Operation(summary = "Delete an achievement")
    public ResponseEntity<Void> deleteAchievement(@PathVariable UUID achievementId) {
        achievementService.deleteAchievement(achievementId);
        return ResponseEntity.noContent().build();
    }

    // --- Skills ---

    @GetMapping("/{userId}/skills")
    @Operation(summary = "Get user skills")
    public ResponseEntity<List<PortfolioSkillDto>> getSkills(@PathVariable UUID userId) {
        return ResponseEntity.ok(skillService.getSkillsByUserId(userId));
    }

    @PostMapping("/{userId}/skills")
    @Operation(summary = "Add a skill")
    public ResponseEntity<PortfolioSkillDto> addSkill(@PathVariable UUID userId, @RequestBody CreateSkillDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.addSkill(userId, dto));
    }

    @PutMapping("/skills/{skillId}/level")
    @Operation(summary = "Update skill level")
    public ResponseEntity<PortfolioSkillDto> updateSkillLevel(@PathVariable UUID skillId, @RequestParam Integer level) {
        return ResponseEntity.ok(skillService.updateSkillLevel(skillId, level));
    }

    @LecturerOrAbove
    @PatchMapping("/skills/{skillId}/verify")
    @Operation(summary = "Verify a skill (LECTURER+)")
    public ResponseEntity<PortfolioSkillDto> verifySkill(@PathVariable UUID skillId, @RequestParam UUID verifiedBy) {
        return ResponseEntity.ok(skillService.verifySkill(skillId, verifiedBy));
    }

    @DeleteMapping("/skills/{skillId}")
    @Operation(summary = "Delete a skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable UUID skillId) {
        skillService.deleteSkill(skillId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by skill")
    public ResponseEntity<List<PortfolioSkillDto>> searchBySkill(
            @RequestParam String skill, @RequestParam(defaultValue = "1") Integer minLevel) {
        return ResponseEntity.ok(skillService.searchBySkill(skill, minLevel));
    }

    // --- Reviews ---

    @GetMapping("/{userId}/reviews")
    @Operation(summary = "Get user reviews")
    public ResponseEntity<List<ReviewDto>> getReviews(@PathVariable UUID userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUserId(userId));
    }

    @PostMapping("/{userId}/reviews")
    @Operation(summary = "Leave a review")
    public ResponseEntity<ReviewDto> addReview(@PathVariable UUID userId, @RequestBody CreateReviewDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(userId, dto));
    }

    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "Delete a review")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
