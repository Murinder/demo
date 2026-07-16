package com.example.adminservice.service;

import com.example.adminservice.dto.DataOperationDto;
import com.example.adminservice.model.entity.DataOperation;
import com.example.adminservice.model.enums.OperationStatus;
import com.example.adminservice.model.enums.OperationType;
import com.example.adminservice.repository.DataOperationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataOperationServiceTest {

    @Mock
    private DataOperationRepository dataOperationRepository;

    @InjectMocks
    private DataOperationService dataOperationService;

    @Test
    void startImport_shouldCreatePendingImportOperation() {
        UUID createdBy = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        Map<String, Object> details = Map.of("format", "csv");

        DataOperation saved = DataOperation.builder()
                .id(operationId)
                .operationType(OperationType.IMPORT)
                .entityType("users")
                .filePath("/imports/users.csv")
                .status(OperationStatus.PENDING)
                .startedAt(OffsetDateTime.now())
                .createdBy(createdBy)
                .recordCount(0)
                .details(details)
                .build();

        when(dataOperationRepository.save(any(DataOperation.class))).thenReturn(saved);

        DataOperationDto result = dataOperationService.startImport(
                "users", "/imports/users.csv", createdBy, details);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(operationId);
        assertThat(result.getOperationType()).isEqualTo(OperationType.IMPORT);
        assertThat(result.getStatus()).isEqualTo(OperationStatus.PENDING);
        assertThat(result.getEntityType()).isEqualTo("users");
        assertThat(result.getRecordCount()).isEqualTo(0);
        verify(dataOperationRepository).save(any(DataOperation.class));
    }

    @Test
    void startExport_shouldCreatePendingExportOperation() {
        UUID createdBy = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        Map<String, Object> details = Map.of("format", "xlsx");

        DataOperation saved = DataOperation.builder()
                .id(operationId)
                .operationType(OperationType.EXPORT)
                .entityType("events")
                .filePath("/exports/events.xlsx")
                .status(OperationStatus.PENDING)
                .startedAt(OffsetDateTime.now())
                .createdBy(createdBy)
                .recordCount(0)
                .details(details)
                .build();

        when(dataOperationRepository.save(any(DataOperation.class))).thenReturn(saved);

        DataOperationDto result = dataOperationService.startExport(
                "events", "/exports/events.xlsx", createdBy, details);

        assertThat(result).isNotNull();
        assertThat(result.getOperationType()).isEqualTo(OperationType.EXPORT);
        assertThat(result.getStatus()).isEqualTo(OperationStatus.PENDING);
        assertThat(result.getEntityType()).isEqualTo("events");
        assertThat(result.getFilePath()).isEqualTo("/exports/events.xlsx");
        verify(dataOperationRepository).save(any(DataOperation.class));
    }

    @Test
    void getAllOperations_shouldReturnAllOrderedByStartedAt() {
        DataOperation op1 = DataOperation.builder()
                .id(UUID.randomUUID())
                .operationType(OperationType.IMPORT)
                .entityType("users")
                .filePath("/imports/users.csv")
                .status(OperationStatus.COMPLETED)
                .startedAt(OffsetDateTime.now().minusHours(2))
                .createdBy(UUID.randomUUID())
                .recordCount(150)
                .build();

        DataOperation op2 = DataOperation.builder()
                .id(UUID.randomUUID())
                .operationType(OperationType.EXPORT)
                .entityType("projects")
                .filePath("/exports/projects.xlsx")
                .status(OperationStatus.PENDING)
                .startedAt(OffsetDateTime.now())
                .createdBy(UUID.randomUUID())
                .recordCount(0)
                .build();

        when(dataOperationRepository.findAllByOrderByStartedAtDesc()).thenReturn(List.of(op2, op1));

        List<DataOperationDto> results = dataOperationService.getAllOperations();

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getOperationType()).isEqualTo(OperationType.EXPORT);
        assertThat(results.get(1).getOperationType()).isEqualTo(OperationType.IMPORT);
        verify(dataOperationRepository).findAllByOrderByStartedAtDesc();
    }

    @Test
    void getAllOperations_whenEmpty_shouldReturnEmptyList() {
        when(dataOperationRepository.findAllByOrderByStartedAtDesc()).thenReturn(List.of());

        List<DataOperationDto> results = dataOperationService.getAllOperations();

        assertThat(results).isEmpty();
    }

    @Test
    void startImport_shouldMapDetailsCorrectly() {
        UUID createdBy = UUID.randomUUID();
        Map<String, Object> details = Map.of("encoding", "UTF-8", "separator", ";");

        DataOperation saved = DataOperation.builder()
                .id(UUID.randomUUID())
                .operationType(OperationType.IMPORT)
                .entityType("grades")
                .filePath("/imports/grades.csv")
                .status(OperationStatus.PENDING)
                .startedAt(OffsetDateTime.now())
                .createdBy(createdBy)
                .recordCount(0)
                .details(details)
                .build();

        when(dataOperationRepository.save(any(DataOperation.class))).thenReturn(saved);

        DataOperationDto result = dataOperationService.startImport(
                "grades", "/imports/grades.csv", createdBy, details);

        assertThat(result.getDetails()).containsEntry("encoding", "UTF-8");
        assertThat(result.getDetails()).containsEntry("separator", ";");
        assertThat(result.getCreatedBy()).isEqualTo(createdBy);
    }
}
