package com.example.partnerservice.controller;

import com.example.partnerservice.dto.CreateVacancyDto;
import com.example.partnerservice.dto.VacancyDto;
import com.example.partnerservice.service.VacancyService;
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
@RequestMapping("/api/v1/vacancies")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Vacancies", description = "Partner vacancy management APIs")
public class VacancyController {

    private final VacancyService vacancyService;

    @PostMapping
    @Operation(summary = "Create a new vacancy")
    public ResponseEntity<VacancyDto> create(@RequestBody CreateVacancyDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vacancyService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Get all vacancies")
    public ResponseEntity<List<VacancyDto>> getAll() {
        return ResponseEntity.ok(vacancyService.getAll());
    }

    @GetMapping("/{vacancyId}")
    @Operation(summary = "Get vacancy by ID")
    public ResponseEntity<VacancyDto> getById(@PathVariable UUID vacancyId) {
        return ResponseEntity.ok(vacancyService.getById(vacancyId));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active vacancies")
    public ResponseEntity<List<VacancyDto>> getActiveVacancies() {
        return ResponseEntity.ok(vacancyService.getActiveVacancies());
    }

    @GetMapping("/partner/{partnerId}")
    @Operation(summary = "Get vacancies by partner")
    public ResponseEntity<List<VacancyDto>> getByPartnerId(@PathVariable UUID partnerId) {
        return ResponseEntity.ok(vacancyService.getByPartnerId(partnerId));
    }
}
