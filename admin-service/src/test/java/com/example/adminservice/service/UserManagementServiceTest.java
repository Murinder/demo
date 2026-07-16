package com.example.adminservice.service;

import com.example.adminservice.dto.ChangeRoleRequest;
import com.example.adminservice.dto.UserRoleDto;
import com.example.adminservice.model.entity.UserRoleEntity;
import com.example.adminservice.model.enums.SystemRole;
import com.example.adminservice.repository.UserRoleRepository;
import com.example.sharedlib.event.EventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private UserManagementService userManagementService;

    @Test
    void getAllUserRoles_shouldReturnAllRoles() {
        UUID userId = UUID.randomUUID();
        UserRoleEntity entity = UserRoleEntity.builder()
                .id(new UserRoleEntity.UserRolePK(userId, SystemRole.STUDENT))
                .assignedAt(OffsetDateTime.now())
                .assignedBy(UUID.randomUUID())
                .build();

        when(userRoleRepository.findAll()).thenReturn(List.of(entity));

        List<UserRoleDto> results = userManagementService.getAllUserRoles();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getUserId()).isEqualTo(userId);
        assertThat(results.get(0).getRole()).isEqualTo(SystemRole.STUDENT);
        verify(userRoleRepository).findAll();
    }

    @Test
    void getUserRoles_shouldReturnRolesForUser() {
        UUID userId = UUID.randomUUID();
        UserRoleEntity entity = UserRoleEntity.builder()
                .id(new UserRoleEntity.UserRolePK(userId, SystemRole.LECTURER))
                .assignedAt(OffsetDateTime.now())
                .assignedBy(UUID.randomUUID())
                .build();

        when(userRoleRepository.findByIdUserId(userId)).thenReturn(List.of(entity));

        List<UserRoleDto> results = userManagementService.getUserRoles(userId);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getRole()).isEqualTo(SystemRole.LECTURER);
        verify(userRoleRepository).findByIdUserId(userId);
    }

    @Test
    void getUserRoles_whenNoRoles_shouldReturnEmptyList() {
        UUID userId = UUID.randomUUID();
        when(userRoleRepository.findByIdUserId(userId)).thenReturn(List.of());

        List<UserRoleDto> results = userManagementService.getUserRoles(userId);

        assertThat(results).isEmpty();
    }

    @Test
    void changeUserRole_shouldDeleteOldAndAssignNew() {
        UUID userId = UUID.randomUUID();
        UUID assignedBy = UUID.randomUUID();
        ChangeRoleRequest request = new ChangeRoleRequest(SystemRole.ADMIN);

        UserRoleEntity existingRole = UserRoleEntity.builder()
                .id(new UserRoleEntity.UserRolePK(userId, SystemRole.STUDENT))
                .assignedAt(OffsetDateTime.now().minusDays(30))
                .assignedBy(UUID.randomUUID())
                .build();

        UserRoleEntity savedRole = UserRoleEntity.builder()
                .id(new UserRoleEntity.UserRolePK(userId, SystemRole.ADMIN))
                .assignedAt(OffsetDateTime.now())
                .assignedBy(assignedBy)
                .build();

        when(userRoleRepository.findByIdUserId(userId)).thenReturn(List.of(existingRole));
        when(userRoleRepository.save(any(UserRoleEntity.class))).thenReturn(savedRole);

        UserRoleDto result = userManagementService.changeUserRole(userId, request, assignedBy);

        assertThat(result.getRole()).isEqualTo(SystemRole.ADMIN);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getAssignedBy()).isEqualTo(assignedBy);
        verify(userRoleRepository).deleteByIdUserId(userId);
        verify(userRoleRepository).save(any(UserRoleEntity.class));
        verify(eventPublisher).publish(anyString(), anyString(), any());
    }

    @Test
    void changeUserRole_whenNoExistingRoles_shouldAssignNewRole() {
        UUID userId = UUID.randomUUID();
        UUID assignedBy = UUID.randomUUID();
        ChangeRoleRequest request = new ChangeRoleRequest(SystemRole.LECTURER);

        UserRoleEntity savedRole = UserRoleEntity.builder()
                .id(new UserRoleEntity.UserRolePK(userId, SystemRole.LECTURER))
                .assignedAt(OffsetDateTime.now())
                .assignedBy(assignedBy)
                .build();

        when(userRoleRepository.findByIdUserId(userId)).thenReturn(List.of());
        when(userRoleRepository.save(any(UserRoleEntity.class))).thenReturn(savedRole);

        UserRoleDto result = userManagementService.changeUserRole(userId, request, assignedBy);

        assertThat(result.getRole()).isEqualTo(SystemRole.LECTURER);
        verify(userRoleRepository).deleteByIdUserId(userId);
        verify(eventPublisher).publish(anyString(), anyString(), any());
    }
}
