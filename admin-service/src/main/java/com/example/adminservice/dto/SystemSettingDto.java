package com.example.adminservice.dto;

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
public class SystemSettingDto {
    private String key;
    private String value;
    private String description;
    private OffsetDateTime updatedAt;
    private UUID updatedBy;
}
