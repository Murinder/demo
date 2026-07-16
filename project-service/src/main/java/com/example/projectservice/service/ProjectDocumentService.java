package com.example.projectservice.service;

import com.example.projectservice.dto.ProjectDocumentDto;
import com.example.projectservice.exception.DocumentNotFoundException;
import com.example.projectservice.model.Project;
import com.example.projectservice.model.ProjectDocument;
import com.example.projectservice.repository.ProjectDocumentRepository;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.sharedlib.event.EventPublisher;
import com.example.sharedlib.event.ProjectEvent;
import com.example.sharedlib.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectDocumentService {

    private static final String BUCKET_NAME = "project-documents";

    private final ProjectDocumentRepository projectDocumentRepository;
    private final ProjectService projectService;
    private final FileStorageService fileStorageService;
    private final EventPublisher eventPublisher;

    @Transactional
    public ProjectDocumentDto uploadDocument(UUID projectId, UUID userId, String description, MultipartFile file) {
        Project project = projectService.getProjectEntityById(projectId);

        String objectName = projectId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            fileStorageService.ensureBucketExists(BUCKET_NAME);
            fileStorageService.upload(BUCKET_NAME, objectName,
                    file.getInputStream(), file.getContentType(), file.getSize());
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload document", e);
        }
        String storagePath = objectName;

        ProjectDocument document = ProjectDocument.builder()
                .project(project)
                .filePath(storagePath)
                .version(1)
                .uploadedBy(userId)
                .description(description)
                .build();
        ProjectDocument savedDocument = projectDocumentRepository.save(document);

        // Publish document_uploaded event
        ProjectEvent event = ProjectEvent.builder()
                .documentId(savedDocument.getId())
                .projectId(projectId)
                .title(file.getOriginalFilename())
                .build();
        event.init("project-service");
        eventPublisher.publish(RabbitMqAutoConfiguration.PROJECT_EXCHANGE, "project.document_uploaded", event);

        return toDto(savedDocument);
    }

    public record DocumentDownload(Resource resource, String filename) {}

    @Transactional(readOnly = true)
    public DocumentDownload downloadDocument(UUID documentId) {
        ProjectDocument document = projectDocumentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + documentId));

        InputStream inputStream = fileStorageService.download(BUCKET_NAME, document.getFilePath());
        String filename = extractFilename(document.getFilePath());
        return new DocumentDownload(new InputStreamResource(inputStream), filename);
    }

    private String extractFilename(String filePath) {
        String name = filePath.substring(filePath.lastIndexOf('/') + 1);
        int underscoreIdx = name.indexOf('_');
        return underscoreIdx >= 0 ? name.substring(underscoreIdx + 1) : name;
    }

    @Transactional(readOnly = true)
    public ProjectDocumentDto getDocumentById(UUID documentId) {
        return projectDocumentRepository.findById(documentId)
                .map(this::toDto)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + documentId));
    }

    @Transactional(readOnly = true)
    public List<ProjectDocumentDto> getDocumentsByProjectId(UUID projectId) {
        return projectDocumentRepository.findByProjectId(projectId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void deleteDocument(UUID documentId) {
        ProjectDocument document = projectDocumentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + documentId));

        fileStorageService.delete(BUCKET_NAME, document.getFilePath());
        projectDocumentRepository.deleteById(documentId);
    }

    private ProjectDocumentDto toDto(ProjectDocument document) {
        return ProjectDocumentDto.builder()
                .id(document.getId())
                .projectId(document.getProject().getId())
                .taskId(document.getTaskId())
                .filePath(document.getFilePath())
                .version(document.getVersion())
                .uploadedBy(document.getUploadedBy())
                .createdAt(document.getCreatedAt())
                .description(document.getDescription())
                .build();
    }
}
