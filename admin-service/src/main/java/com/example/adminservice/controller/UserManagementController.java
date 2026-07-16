package com.example.adminservice.controller;

import com.example.adminservice.dto.ChangeRoleRequest;
import com.example.adminservice.dto.UserRoleDto;
import com.example.adminservice.service.UserManagementService;
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
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@AdminOnly
@Tag(name = "User Management", description = "Управление пользователями и их ролями")
public class UserManagementController {

    private final UserManagementService userManagementService;

    @Operation(summary = "Get all users with roles")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserRoleDto>>> getAllUserRoles() {
        List<UserRoleDto> roles = userManagementService.getAllUserRoles();
        return ResponseEntity.ok(ApiResponse.success(roles));
    }

    @Operation(summary = "Change user role")
    @PutMapping("/{userId}/role")
    public ResponseEntity<ApiResponse<UserRoleDto>> changeUserRole(
            @PathVariable UUID userId,
            @RequestBody ChangeRoleRequest request,
            Authentication authentication) {
        UUID assignedBy = UUID.fromString(authentication.getName());
        UserRoleDto result = userManagementService.changeUserRole(userId, request, assignedBy);
        return ResponseEntity.ok(ApiResponse.success(result, "User role updated successfully"));
    }
}
