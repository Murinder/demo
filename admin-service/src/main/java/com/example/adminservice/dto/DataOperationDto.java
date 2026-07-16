package com.example.adminservice.dto;

import com.example.adminservice.model.enums.OperationStatus;
import com.example.adminservice.model.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataOperationDto {
    private UUID id;
    private OperationType operationType;
    private String entityType;
    private String filePath;
    private OperationStatus status;
    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;
    private String errorMessage;
    private UUID createdBy;
    private Integer recordCount;
    private Map<String, Object> details;
}
