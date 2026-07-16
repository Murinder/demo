package com.example.ratingservice.controller;

import com.example.ratingservice.dto.RatingCriteriaDto;
import com.example.ratingservice.service.RatingCriteriaService;
import com.example.sharedlib.security.AdminOnly;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ratings/criteria")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Rating Criteria", description = "Rating criteria management APIs")
public class RatingCriteriaController {

    private final RatingCriteriaService criteriaService;

    @GetMapping
    @Operation(summary = "Get all criteria")
    public ResponseEntity<List<RatingCriteriaDto>> getAllCriteria() {
        return ResponseEntity.ok(criteriaService.getAllCriteria());
    }

    @AdminOnly
    @PostMapping
    @Operation(summary = "Create a rating criteria (ADMIN)")
    public ResponseEntity<RatingCriteriaDto> createCriteria(@RequestBody RatingCriteriaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(criteriaService.createCriteria(dto));
    }

    @AdminOnly
    @PutMapping("/{criteriaId}")
    @Operation(summary = "Update a rating criteria (ADMIN)")
    public ResponseEntity<RatingCriteriaDto> updateCriteria(@PathVariable UUID criteriaId, @RequestBody RatingCriteriaDto dto) {
        return ResponseEntity.ok(criteriaService.updateCriteria(criteriaId, dto));
    }
}
