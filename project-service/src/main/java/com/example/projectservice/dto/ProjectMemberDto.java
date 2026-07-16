package com.example.projectservice.dto;

import com.example.sharedlib.enums.ProjectRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberDto {
    private UUID projectId;
    private UUID userId;
    private ProjectRole role;
}