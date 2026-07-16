package com.example.partnerservice.controller;

import com.example.partnerservice.dto.CreatePartnerDto;
import com.example.partnerservice.dto.PartnerDto;
import com.example.partnerservice.model.enums.PartnershipStatus;
import com.example.partnerservice.service.PartnerService;
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
@RequestMapping("/api/v1/partners")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Partners", description = "Partner management APIs")
public class PartnerController {

    private final PartnerService partnerService;

    @PostMapping("/register")
    @Operation(summary = "Register a new partner")
    public ResponseEntity<PartnerDto> register(@RequestBody CreatePartnerDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Get all partners")
    public ResponseEntity<List<PartnerDto>> getAll() {
        return ResponseEntity.ok(partnerService.getAll());
    }

    @GetMapping("/{partnerId}")
    @Operation(summary = "Get partner by ID")
    public ResponseEntity<PartnerDto> getById(@PathVariable UUID partnerId) {
        return ResponseEntity.ok(partnerService.getById(partnerId));
    }

    @PutMapping("/{partnerId}")
    @Operation(summary = "Update partner")
    public ResponseEntity<PartnerDto> update(@PathVariable UUID partnerId, @RequestBody CreatePartnerDto dto) {
        return ResponseEntity.ok(partnerService.update(partnerId, dto));
    }

    @PatchMapping("/{partnerId}/status")
    @Operation(summary = "Update partner status")
    public ResponseEntity<PartnerDto> updateStatus(@PathVariable UUID partnerId, @RequestParam PartnershipStatus status) {
        return ResponseEntity.ok(partnerService.updateStatus(partnerId, status));
    }
}
