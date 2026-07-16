package com.example.eventservice.controller;

import com.example.eventservice.dto.CreateDefenseDto;
import com.example.eventservice.dto.DefenseDto;
import com.example.eventservice.service.DefenseService;
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
@RequestMapping("/api/v1/events/defenses")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Defenses", description = "Defense schedule APIs")
public class DefenseController {

    private final DefenseService defenseService;

    @GetMapping
    @Operation(summary = "Get defenses by supervisor or student")
    public ResponseEntity<ApiResponse<List<DefenseDto>>> getDefenses(
            @RequestParam(required = false) UUID supervisorId,
            @RequestParam(required = false) UUID studentId) {
        List<DefenseDto> defenses;
        if (supervisorId != null) {
            defenses = defenseService.getDefensesBySupervisor(supervisorId);
        } else if (studentId != null) {
            defenses = defenseService.getDefensesByStudent(studentId);
        } else {
            defenses = List.of();
        }
        return ResponseEntity.ok(ApiResponse.success(defenses));
    }

    @PostMapping
    @Operation(summary = "Create a defense")
    public ResponseEntity<ApiResponse<DefenseDto>> createDefense(@RequestBody CreateDefenseDto dto) {
        DefenseDto defense = defenseService.createDefense(dto);
        return ResponseEntity.ok(ApiResponse.success(defense, "Defense created"));
    }

    @PatchMapping("/{defenseId}/status")
    @Operation(summary = "Update defense status and grade")
    public ResponseEntity<ApiResponse<DefenseDto>> updateDefenseStatus(
            @PathVariable UUID defenseId,
            @RequestParam String status,
            @RequestParam(required = false) Integer grade) {
        DefenseDto defense = defenseService.updateStatus(defenseId, status, grade);
        return ResponseEntity.ok(ApiResponse.success(defense, "Defense status updated"));
    }
}
