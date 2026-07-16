package com.example.coreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto {
    private UUID id;
    private String name;
    private String code;
    private UUID facultyId;
    private String facultyName;
    private UUID headUserId;
    private String description;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
