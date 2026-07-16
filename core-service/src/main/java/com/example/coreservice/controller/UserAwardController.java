package com.example.coreservice.controller;

import com.example.coreservice.model.entity.UserAward;
import com.example.coreservice.repository.UserAwardRepository;
import com.example.sharedlib.response.ApiResponse;
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
@RequestMapping("/api/v1/user-awards")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "User Awards", description = "API for managing user awards")
public class UserAwardController {

    private final UserAwardRepository userAwardRepository;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all awards for a specific user")
    public ResponseEntity<ApiResponse<List<UserAward>>> getByUser(@PathVariable UUID userId) {
        List<UserAward> awards = userAwardRepository.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.<List<UserAward>>builder()
                .success(true)
                .code("AWARDS_RETRIEVED")
                .message("User awards retrieved successfully")
                .data(awards)
                .build());
    }

    @PostMapping
    @Operation(summary = "Create a new user award")
    public ResponseEntity<ApiResponse<UserAward>> create(@RequestBody UserAward award) {
        if (award.getId() == null) {
            award.setId(UUID.randomUUID());
        }
        UserAward saved = userAwardRepository.save(award);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<UserAward>builder()
                        .success(true)
                        .code("AWARD_CREATED")
                        .message("User award created successfully")
                        .data(saved)
                        .build());
    }

    @DeleteMapping("/{awardId}")
    @Operation(summary = "Delete a user award")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID awardId) {
        userAwardRepository.deleteById(awardId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .code("AWARD_DELETED")
                .message("User award deleted successfully")
                .build());
    }
}
