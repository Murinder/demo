package com.example.partnerservice.controller;

import com.example.partnerservice.dto.CaseDto;
import com.example.partnerservice.dto.CreateCaseDto;
import com.example.partnerservice.service.CaseService;
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
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Cases", description = "Partner case management APIs")
public class CaseController {

    private final CaseService caseService;

    @PostMapping
    @Operation(summary = "Create a new case")
    public ResponseEntity<CaseDto> create(@RequestBody CreateCaseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(caseService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Get all cases")
    public ResponseEntity<List<CaseDto>> getAll() {
        return ResponseEntity.ok(caseService.getAll());
    }

    @GetMapping("/{caseId}")
    @Operation(summary = "Get case by ID")
    public ResponseEntity<CaseDto> getById(@PathVariable UUID caseId) {
        return ResponseEntity.ok(caseService.getById(caseId));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active cases")
    public ResponseEntity<List<CaseDto>> getActiveCases() {
        return ResponseEntity.ok(caseService.getActiveCases());
    }

    @GetMapping("/partner/{partnerId}")
    @Operation(summary = "Get cases by partner")
    public ResponseEntity<List<CaseDto>> getByPartnerId(@PathVariable UUID partnerId) {
        return ResponseEntity.ok(caseService.getByPartnerId(partnerId));
    }
}
