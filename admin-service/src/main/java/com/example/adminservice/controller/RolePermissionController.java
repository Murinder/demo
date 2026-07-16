package com.example.adminservice.controller;

import com.example.adminservice.dto.RolePermissionDto;
import com.example.adminservice.model.enums.SystemRole;
import com.example.adminservice.service.RolePermissionService;
import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.security.AdminOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/roles")
@RequiredArgsConstructor
@AdminOnly
@Tag(name = "Roles & Permissions", description = "Роли и права доступа")
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    @Operation(summary = "Get all role permissions")
    @GetMapping
    public ResponseEntity<ApiResponse<List<RolePermissionDto>>> getAllPermissions() {
        List<RolePermissionDto> permissions = rolePermissionService.getAllPermissions();
        return ResponseEntity.ok(ApiResponse.success(permissions));
    }

    @Operation(summary = "Add permission to role")
    @PostMapping("/{role}/permissions")
    public ResponseEntity<ApiResponse<RolePermissionDto>> addPermission(
            @PathVariable SystemRole role,
            @RequestBody RolePermissionDto dto) {
        RolePermissionDto result = rolePermissionService.addPermission(role, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Permission added successfully"));
    }

    @Operation(summary = "Delete permission from role")
    @DeleteMapping("/{role}/permissions/{permission}")
    public ResponseEntity<ApiResponse<Void>> deletePermission(
            @PathVariable SystemRole role,
            @PathVariable String permission) {
        rolePermissionService.deletePermission(role, permission);
        return ResponseEntity.ok(ApiResponse.success(null, "Permission deleted successfully"));
    }
}
