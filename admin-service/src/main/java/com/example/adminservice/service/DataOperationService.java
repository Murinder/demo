package com.example.adminservice.service;

import com.example.adminservice.dto.DataOperationDto;
import com.example.adminservice.model.entity.DataOperation;
import com.example.adminservice.model.enums.OperationStatus;
import com.example.adminservice.model.enums.OperationType;
import com.example.adminservice.repository.DataOperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataOperationService {

    private final DataOperationRepository dataOperationRepository;

    @Transactional
    public DataOperationDto startImport(String entityType, String filePath, UUID createdBy,
                                        Map<String, Object> details) {
        DataOperation operation = DataOperation.builder()
                .operationType(OperationType.IMPORT)
                .entityType(entityType)
                .filePath(filePath)
                .status(OperationStatus.PENDING)
                .startedAt(OffsetDateTime.now())
                .createdBy(createdBy)
                .recordCount(0)
                .details(details)
                .build();

        DataOperation saved = dataOperationRepository.save(operation);
        log.info("Started import operation: id={}, entityType={}", saved.getId(), entityType);
        return toDto(saved);
    }

    @Transactional
    public DataOperationDto startExport(String entityType, String filePath, UUID createdBy,
                                        Map<String, Object> details) {
        DataOperation operation = DataOperation.builder()
                .operationType(OperationType.EXPORT)
                .entityType(entityType)
                .filePath(filePath)
                .status(OperationStatus.PENDING)
                .startedAt(OffsetDateTime.now())
                .createdBy(createdBy)
                .recordCount(0)
                .details(details)
                .build();

        DataOperation saved = dataOperationRepository.save(operation);
        log.info("Started export operation: id={}, entityType={}", saved.getId(), entityType);
        return toDto(saved);
    }

    public List<DataOperationDto> getAllOperations() {
        return dataOperationRepository.findAllByOrderByStartedAtDesc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private DataOperationDto toDto(DataOperation entity) {
        return DataOperationDto.builder()
                .id(entity.getId())
                .operationType(entity.getOperationType())
                .entityType(entity.getEntityType())
                .filePath(entity.getFilePath())
                .status(entity.getStatus())
                .startedAt(entity.getStartedAt())
                .completedAt(entity.getCompletedAt())
                .errorMessage(entity.getErrorMessage())
                .createdBy(entity.getCreatedBy())
                .recordCount(entity.getRecordCount())
                .details(entity.getDetails())
                .build();
    }
}
