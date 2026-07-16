package com.example.adminservice.dto;

import com.example.adminservice.model.enums.PermissionType;
import com.example.adminservice.model.enums.SystemRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermissionDto {
    private SystemRole role;
    private String permission;
    private PermissionType permissionType;
    private String resource;
}
