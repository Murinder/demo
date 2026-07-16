package com.example.ratingservice.controller;

import com.example.ratingservice.dto.DepartmentRatingDto;
import com.example.ratingservice.service.DepartmentRatingService;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ratings/departments")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Department Ratings", description = "Department rating APIs")
public class DepartmentRatingController {

    private final DepartmentRatingService departmentRatingService;

    @GetMapping("/{departmentId}")
    @Operation(summary = "Get department rating")
    public ResponseEntity<DepartmentRatingDto> getDepartmentRating(@PathVariable UUID departmentId) {
        return ResponseEntity.ok(departmentRatingService.getDepartmentRating(departmentId));
    }

    @GetMapping("/ranking")
    @Operation(summary = "Get all departments ranked")
    public ResponseEntity<List<DepartmentRatingDto>> getDepartmentRanking() {
        return ResponseEntity.ok(departmentRatingService.getDepartmentRanking());
    }
}
