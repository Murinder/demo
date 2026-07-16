package com.example.adminservice.service;

import com.example.adminservice.dto.ChangeRoleRequest;
import com.example.adminservice.dto.UserRoleDto;
import com.example.adminservice.model.entity.UserRoleEntity;
import com.example.adminservice.model.enums.SystemRole;
import com.example.adminservice.repository.UserRoleRepository;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.sharedlib.event.EventPublisher;
import com.example.sharedlib.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRoleRepository userRoleRepository;
    private final EventPublisher eventPublisher;

    public List<UserRoleDto> getAllUserRoles() {
        return userRoleRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<UserRoleDto> getUserRoles(UUID userId) {
        return userRoleRepository.findByIdUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserRoleDto changeUserRole(UUID userId, ChangeRoleRequest request, UUID assignedBy) {
        // Find existing roles for the user
        List<UserRoleEntity> existingRoles = userRoleRepository.findByIdUserId(userId);
        String oldRole = existingRoles.isEmpty() ? null :
                existingRoles.get(0).getId().getRole().name();

        // Remove existing roles
        userRoleRepository.deleteByIdUserId(userId);

        // Assign new role
        UserRoleEntity newRoleEntity = UserRoleEntity.builder()
                .id(new UserRoleEntity.UserRolePK(userId, request.getRole()))
                .assignedAt(OffsetDateTime.now())
                .assignedBy(assignedBy)
                .build();

        UserRoleEntity saved = userRoleRepository.save(newRoleEntity);
        log.info("Changed role for user {} to {}", userId, request.getRole());

        // Publish role changed event
        publishRoleChangedEvent(userId, oldRole, request.getRole().name());

        return toDto(saved);
    }

    private void publishRoleChangedEvent(UUID userId, String oldRole, String newRole) {
        UserEvent event = UserEvent.builder()
                .userId(userId)
                .oldRole(oldRole)
                .newRole(newRole)
                .build();
        event.init("admin-service");

        eventPublisher.publish(
                RabbitMqAutoConfiguration.USER_EXCHANGE,
                RabbitMqAutoConfiguration.USER_ROLE_CHANGED_KEY,
                event
        );
    }

    private UserRoleDto toDto(UserRoleEntity entity) {
        return UserRoleDto.builder()
                .userId(entity.getId().getUserId())
                .role(entity.getId().getRole())
                .assignedAt(entity.getAssignedAt())
                .assignedBy(entity.getAssignedBy())
                .build();
    }
}
