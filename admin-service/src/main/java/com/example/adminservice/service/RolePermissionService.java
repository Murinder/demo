package com.example.adminservice.service;

import com.example.adminservice.dto.RolePermissionDto;
import com.example.adminservice.model.entity.RolePermission;
import com.example.adminservice.model.enums.PermissionType;
import com.example.adminservice.model.enums.SystemRole;
import com.example.adminservice.repository.RolePermissionRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;

    public List<RolePermissionDto> getAllPermissions() {
        return rolePermissionRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<RolePermissionDto> getPermissionsByRole(SystemRole role) {
        return rolePermissionRepository.findByIdRole(role).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RolePermissionDto addPermission(SystemRole role, RolePermissionDto dto) {
        RolePermission entity = RolePermission.builder()
                .id(new RolePermission.RolePermissionPK(role, dto.getPermission()))
                .permissionType(dto.getPermissionType() != null ? dto.getPermissionType() : PermissionType.READ)
                .resource(dto.getResource())
                .build();

        RolePermission saved = rolePermissionRepository.save(entity);
        log.info("Added permission {} to role {}", dto.getPermission(), role);
        return toDto(saved);
    }

    @Transactional
    public void deletePermission(SystemRole role, String permission) {
        RolePermission.RolePermissionPK pk = new RolePermission.RolePermissionPK(role, permission);
        if (!rolePermissionRepository.existsById(pk)) {
            throw new ResourceNotFoundException("RolePermission", "role/permission", role + "/" + permission);
        }
        rolePermissionRepository.deleteById(pk);
        log.info("Deleted permission {} from role {}", permission, role);
    }

    private RolePermissionDto toDto(RolePermission entity) {
        return RolePermissionDto.builder()
                .role(entity.getId().getRole())
                .permission(entity.getId().getPermission())
                .permissionType(entity.getPermissionType())
                .resource(entity.getResource())
                .build();
    }
}
