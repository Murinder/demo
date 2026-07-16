package com.example.adminservice.service;

import com.example.adminservice.dto.RolePermissionDto;
import com.example.adminservice.model.entity.RolePermission;
import com.example.adminservice.model.enums.PermissionType;
import com.example.adminservice.model.enums.SystemRole;
import com.example.adminservice.repository.RolePermissionRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolePermissionServiceTest {

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @InjectMocks
    private RolePermissionService rolePermissionService;

    @Test
    void getAllPermissions_shouldReturnAll() {
        RolePermission permission = RolePermission.builder()
                .id(new RolePermission.RolePermissionPK(SystemRole.ADMIN, "users.manage"))
                .permissionType(PermissionType.ADMIN)
                .resource("users")
                .build();

        when(rolePermissionRepository.findAll()).thenReturn(List.of(permission));

        List<RolePermissionDto> results = rolePermissionService.getAllPermissions();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getRole()).isEqualTo(SystemRole.ADMIN);
        assertThat(results.get(0).getPermission()).isEqualTo("users.manage");
        assertThat(results.get(0).getPermissionType()).isEqualTo(PermissionType.ADMIN);
        verify(rolePermissionRepository).findAll();
    }

    @Test
    void getPermissionsByRole_shouldReturnRolePermissions() {
        RolePermission p1 = RolePermission.builder()
                .id(new RolePermission.RolePermissionPK(SystemRole.LECTURER, "projects.read"))
                .permissionType(PermissionType.READ)
                .resource("projects")
                .build();
        RolePermission p2 = RolePermission.builder()
                .id(new RolePermission.RolePermissionPK(SystemRole.LECTURER, "projects.write"))
                .permissionType(PermissionType.WRITE)
                .resource("projects")
                .build();

        when(rolePermissionRepository.findByIdRole(SystemRole.LECTURER)).thenReturn(List.of(p1, p2));

        List<RolePermissionDto> results = rolePermissionService.getPermissionsByRole(SystemRole.LECTURER);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(RolePermissionDto::getPermission)
                .containsExactlyInAnyOrder("projects.read", "projects.write");
    }

    @Test
    void getPermissionsByRole_whenNoPermissions_shouldReturnEmptyList() {
        when(rolePermissionRepository.findByIdRole(SystemRole.PARTNER)).thenReturn(List.of());

        List<RolePermissionDto> results = rolePermissionService.getPermissionsByRole(SystemRole.PARTNER);

        assertThat(results).isEmpty();
    }

    @Test
    void addPermission_shouldSaveAndReturnDto() {
        RolePermissionDto dto = RolePermissionDto.builder()
                .permission("events.create")
                .permissionType(PermissionType.WRITE)
                .resource("events")
                .build();

        RolePermission saved = RolePermission.builder()
                .id(new RolePermission.RolePermissionPK(SystemRole.LECTURER, "events.create"))
                .permissionType(PermissionType.WRITE)
                .resource("events")
                .build();

        when(rolePermissionRepository.save(any(RolePermission.class))).thenReturn(saved);

        RolePermissionDto result = rolePermissionService.addPermission(SystemRole.LECTURER, dto);

        assertThat(result.getRole()).isEqualTo(SystemRole.LECTURER);
        assertThat(result.getPermission()).isEqualTo("events.create");
        assertThat(result.getPermissionType()).isEqualTo(PermissionType.WRITE);
        assertThat(result.getResource()).isEqualTo("events");
        verify(rolePermissionRepository).save(any(RolePermission.class));
    }

    @Test
    void addPermission_withNullPermissionType_shouldDefaultToRead() {
        RolePermissionDto dto = RolePermissionDto.builder()
                .permission("reports.view")
                .permissionType(null)
                .resource("reports")
                .build();

        RolePermission saved = RolePermission.builder()
                .id(new RolePermission.RolePermissionPK(SystemRole.STUDENT, "reports.view"))
                .permissionType(PermissionType.READ)
                .resource("reports")
                .build();

        when(rolePermissionRepository.save(any(RolePermission.class))).thenReturn(saved);

        RolePermissionDto result = rolePermissionService.addPermission(SystemRole.STUDENT, dto);

        assertThat(result.getPermissionType()).isEqualTo(PermissionType.READ);
    }

    @Test
    void deletePermission_whenExists_shouldDelete() {
        RolePermission.RolePermissionPK pk =
                new RolePermission.RolePermissionPK(SystemRole.STUDENT, "settings.read");

        when(rolePermissionRepository.existsById(pk)).thenReturn(true);

        rolePermissionService.deletePermission(SystemRole.STUDENT, "settings.read");

        verify(rolePermissionRepository).deleteById(pk);
    }

    @Test
    void deletePermission_whenNotExists_shouldThrow() {
        RolePermission.RolePermissionPK pk =
                new RolePermission.RolePermissionPK(SystemRole.STUDENT, "nonexistent");

        when(rolePermissionRepository.existsById(pk)).thenReturn(false);

        assertThatThrownBy(() -> rolePermissionService.deletePermission(SystemRole.STUDENT, "nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(rolePermissionRepository, never()).deleteById(any());
    }
}
