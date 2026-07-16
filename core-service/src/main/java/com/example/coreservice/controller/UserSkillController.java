package com.example.coreservice.controller;

import com.example.coreservice.model.dto.UserSkillDto;
import com.example.coreservice.service.UserSkillService;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user-skills")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "User Skills", description = "API for managing user skills")
public class UserSkillController {

    private final UserSkillService userSkillService;

    @GetMapping
    @Operation(summary = "Get all user skills")
    public ResponseEntity<List<UserSkillDto>> getAll() {
        return ResponseEntity.ok(userSkillService.getAllUserSkills());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all skills for a specific user")
    public ResponseEntity<List<UserSkillDto>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userSkillService.getUserSkillsByUserId(userId));
    }

    @GetMapping("/{userId}/{skillName}")
    @Operation(summary = "Get user skill by ID")
    public ResponseEntity<UserSkillDto> getById(@PathVariable UUID userId, @PathVariable String skillName) {
        UserSkillDto userSkillDto = userSkillService.getUserSkillById(userId, skillName);
        return userSkillDto != null ? ResponseEntity.ok(userSkillDto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Create a new user skill")
    public ResponseEntity<UserSkillDto> create(@RequestBody UserSkillDto userSkillDto) {
        return ResponseEntity.ok(userSkillService.createUserSkill(userSkillDto));
    }

    @PutMapping("/{userId}/{skillName}")
    @Operation(summary = "Update an existing user skill")
    public ResponseEntity<UserSkillDto> update(@PathVariable UUID userId, @PathVariable String skillName, @RequestBody UserSkillDto userSkillDto) {
        UserSkillDto updatedUserSkill = userSkillService.updateUserSkill(userId, skillName, userSkillDto);
        return updatedUserSkill != null ? ResponseEntity.ok(updatedUserSkill) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{userId}/{skillName}")
    @Operation(summary = "Delete a user skill")
    public ResponseEntity<Void> delete(@PathVariable UUID userId, @PathVariable String skillName) {
        userSkillService.deleteUserSkill(userId, skillName);
        return ResponseEntity.noContent().build();
    }
}