package com.example.coreservice.controller;

import com.example.coreservice.model.dto.UserLinkDto;
import com.example.coreservice.model.enums.LinkType;
import com.example.coreservice.service.UserLinkService;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user-links")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "User Links", description = "API for managing user links")
public class UserLinkController {

    private final UserLinkService userLinkService;

    @GetMapping
    @Operation(summary = "Get all user links")
    public ResponseEntity<List<UserLinkDto>> getAll() {
        return ResponseEntity.ok(userLinkService.getAllUserLinks());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all links for a specific user")
    public ResponseEntity<List<UserLinkDto>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userLinkService.getUserLinksByUserId(userId));
    }

    @GetMapping("/{userId}/{linkType}")
    @Operation(summary = "Get user link by ID")
    public ResponseEntity<UserLinkDto> getById(@PathVariable UUID userId, @PathVariable LinkType linkType) {
        UserLinkDto userLinkDto = userLinkService.getUserLinkById(userId, linkType);
        return userLinkDto != null ? ResponseEntity.ok(userLinkDto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Create a new user link")
    public ResponseEntity<UserLinkDto> create(@RequestBody UserLinkDto userLinkDto) {
        return ResponseEntity.ok(userLinkService.createUserLink(userLinkDto));
    }

    @PutMapping("/{userId}/{linkType}")
    @Operation(summary = "Update an existing user link")
    public ResponseEntity<UserLinkDto> update(@PathVariable UUID userId, @PathVariable LinkType linkType, @RequestBody UserLinkDto userLinkDto) {
        UserLinkDto updatedUserLink = userLinkService.updateUserLink(userId, linkType, userLinkDto);
        return updatedUserLink != null ? ResponseEntity.ok(updatedUserLink) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{userId}/{linkType}")
    @Operation(summary = "Delete a user link")
    public ResponseEntity<Void> delete(@PathVariable UUID userId, @PathVariable LinkType linkType) {
        userLinkService.deleteUserLink(userId, linkType);
        return ResponseEntity.noContent().build();
    }
}