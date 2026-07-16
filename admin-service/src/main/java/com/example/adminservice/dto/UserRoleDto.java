package com.example.adminservice.dto;

import com.example.adminservice.model.enums.SystemRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleDto {
    private UUID userId;
    private SystemRole role;
    private OffsetDateTime assignedAt;
    private UUID assignedBy;
}
