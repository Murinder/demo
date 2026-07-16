package com.example.coreservice.controller;

import com.example.coreservice.model.dto.UserLanguageDto;
import com.example.coreservice.service.UserLanguageService;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user-languages")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "User Languages", description = "API for managing user languages")
public class UserLanguageController {

    private final UserLanguageService userLanguageService;

    @GetMapping
    @Operation(summary = "Get all user languages")
    public ResponseEntity<List<UserLanguageDto>> getAll() {
        return ResponseEntity.ok(userLanguageService.getAllUserLanguages());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all languages for a specific user")
    public ResponseEntity<List<UserLanguageDto>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userLanguageService.getUserLanguagesByUserId(userId));
    }

    @GetMapping("/{userId}/{language}")
    @Operation(summary = "Get user language by ID")
    public ResponseEntity<UserLanguageDto> getById(@PathVariable UUID userId, @PathVariable String language) {
        UserLanguageDto userLanguageDto = userLanguageService.getUserLanguageById(userId, language);
        return userLanguageDto != null ? ResponseEntity.ok(userLanguageDto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Create a new user language")
    public ResponseEntity<UserLanguageDto> create(@RequestBody UserLanguageDto userLanguageDto) {
        return ResponseEntity.ok(userLanguageService.createUserLanguage(userLanguageDto));
    }

    @PutMapping("/{userId}/{language}")
    @Operation(summary = "Update an existing user language")
    public ResponseEntity<UserLanguageDto> update(@PathVariable UUID userId, @PathVariable String language, @RequestBody UserLanguageDto userLanguageDto) {
        UserLanguageDto updatedUserLanguage = userLanguageService.updateUserLanguage(userId, language, userLanguageDto);
        return updatedUserLanguage != null ? ResponseEntity.ok(updatedUserLanguage) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{userId}/{language}")
    @Operation(summary = "Delete a user language")
    public ResponseEntity<Void> delete(@PathVariable UUID userId, @PathVariable String language) {
        userLanguageService.deleteUserLanguage(userId, language);
        return ResponseEntity.noContent().build();
    }
}