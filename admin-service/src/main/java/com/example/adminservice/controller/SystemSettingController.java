package com.example.adminservice.controller;

import com.example.adminservice.dto.SystemSettingDto;
import com.example.adminservice.service.SystemSettingService;
import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.security.AdminOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/settings")
@RequiredArgsConstructor
@AdminOnly
@Tag(name = "System Settings", description = "Системные настройки")
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    @Operation(summary = "Get all system settings")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SystemSettingDto>>> getAllSettings() {
        List<SystemSettingDto> settings = systemSettingService.getAllSettings();
        return ResponseEntity.ok(ApiResponse.success(settings));
    }

    @Operation(summary = "Update a system setting by key")
    @PutMapping("/{key}")
    public ResponseEntity<ApiResponse<SystemSettingDto>> updateSetting(
            @PathVariable String key,
            @RequestBody SystemSettingDto dto,
            Authentication authentication) {
        UUID updatedBy = UUID.fromString(authentication.getName());
        SystemSettingDto result = systemSettingService.updateSetting(key, dto, updatedBy);
        return ResponseEntity.ok(ApiResponse.success(result, "Setting updated successfully"));
    }
}
