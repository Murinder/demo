package com.example.adminservice.controller;

import com.example.adminservice.dto.DataOperationDto;
import com.example.adminservice.service.DataOperationService;
import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.security.AdminOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/data")
@RequiredArgsConstructor
@AdminOnly
@Tag(name = "Data Operations", description = "Операции с данными")
public class DataOperationController {

    private final DataOperationService dataOperationService;

    @Operation(summary = "Start data import operation")
    @PostMapping("/import")
    public ResponseEntity<ApiResponse<DataOperationDto>> startImport(
            @RequestBody DataOperationDto dto,
            Authentication authentication) {
        UUID createdBy = UUID.fromString(authentication.getName());
        DataOperationDto result = dataOperationService.startImport(
                dto.getEntityType(), dto.getFilePath(), createdBy, dto.getDetails());
        return ResponseEntity.ok(ApiResponse.success(result, "Import operation started"));
    }

    @Operation(summary = "Start data export operation")
    @PostMapping("/export")
    public ResponseEntity<ApiResponse<DataOperationDto>> startExport(
            @RequestBody DataOperationDto dto,
            Authentication authentication) {
        UUID createdBy = UUID.fromString(authentication.getName());
        DataOperationDto result = dataOperationService.startExport(
                dto.getEntityType(), dto.getFilePath(), createdBy, dto.getDetails());
        return ResponseEntity.ok(ApiResponse.success(result, "Export operation started"));
    }

    @Operation(summary = "Get all data operations")
    @GetMapping("/operations")
    public ResponseEntity<ApiResponse<List<DataOperationDto>>> getAllOperations() {
        List<DataOperationDto> operations = dataOperationService.getAllOperations();
        return ResponseEntity.ok(ApiResponse.success(operations));
    }
}
