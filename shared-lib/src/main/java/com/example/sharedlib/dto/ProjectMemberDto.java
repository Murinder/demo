package com.example.sharedlib.dto;

import com.example.sharedlib.enums.ProjectRole;
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
public class ProjectMemberDto {
    private UUID projectId;
    private UUID userId;
    private String userName;
    private ProjectRole role;
    private OffsetDateTime joinedAt;
}