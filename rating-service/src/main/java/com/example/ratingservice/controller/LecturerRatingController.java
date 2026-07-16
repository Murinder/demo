package com.example.ratingservice.controller;

import com.example.ratingservice.dto.StudentRatingDto;
import com.example.ratingservice.service.LecturerRatingService;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ratings/lecturers")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Lecturer Ratings", description = "Lecturer rating APIs")
public class LecturerRatingController {

    private final LecturerRatingService lecturerRatingService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get lecturer rating")
    public ResponseEntity<StudentRatingDto> getLecturerRating(@PathVariable UUID userId) {
        return ResponseEntity.ok(lecturerRatingService.getLecturerRating(userId));
    }
}
