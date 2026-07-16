package com.example.partnerservice.controller;

import com.example.partnerservice.dto.AgreementDto;
import com.example.partnerservice.dto.CreateAgreementDto;
import com.example.partnerservice.model.enums.AgreementStatus;
import com.example.partnerservice.service.AgreementService;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agreements")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Agreements", description = "Partner agreement management APIs")
public class AgreementController {

    private final AgreementService agreementService;

    @PostMapping
    @Operation(summary = "Create a new agreement")
    public ResponseEntity<AgreementDto> create(@RequestBody CreateAgreementDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agreementService.create(dto));
    }

    @GetMapping("/{agreementId}")
    @Operation(summary = "Get agreement by ID")
    public ResponseEntity<AgreementDto> getById(@PathVariable UUID agreementId) {
        return ResponseEntity.ok(agreementService.getById(agreementId));
    }

    @PatchMapping("/{agreementId}/status")
    @Operation(summary = "Update agreement status")
    public ResponseEntity<AgreementDto> updateStatus(@PathVariable UUID agreementId, @RequestParam AgreementStatus status) {
        return ResponseEntity.ok(agreementService.updateStatus(agreementId, status));
    }
}
