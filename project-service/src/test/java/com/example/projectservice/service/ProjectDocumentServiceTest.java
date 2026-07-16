package com.example.projectservice.service;

import com.example.projectservice.dto.ProjectDocumentDto;
import com.example.projectservice.exception.DocumentNotFoundException;
import com.example.projectservice.model.Project;
import com.example.projectservice.model.ProjectDocument;
import com.example.projectservice.repository.ProjectDocumentRepository;
import com.example.sharedlib.enums.ProjectStatus;
import com.example.sharedlib.event.EventPublisher;
import com.example.sharedlib.storage.FileStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectDocumentServiceTest {

    @Mock
    private ProjectDocumentRepository projectDocumentRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private ProjectDocumentService projectDocumentService;

    @Test
    void uploadDocument_SavesAndPublishesEvent() throws IOException {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Project project = Project.builder()
                .id(projectId)
                .title("Test Project")
                .status(ProjectStatus.ACTIVE)
                .createdBy(userId)
                .build();

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.pdf");
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getSize()).thenReturn(1024L);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[1024]));

        when(projectService.getProjectEntityById(projectId)).thenReturn(project);
        when(projectDocumentRepository.save(any(ProjectDocument.class))).thenAnswer(inv -> {
            ProjectDocument doc = inv.getArgument(0);
            doc.setId(UUID.randomUUID());
            return doc;
        });

        ProjectDocumentDto result = projectDocumentService.uploadDocument(projectId, userId, "Test doc", file);

        assertNotNull(result);
        assertEquals(projectId, result.getProjectId());
        assertEquals(userId, result.getUploadedBy());
        assertEquals("Test doc", result.getDescription());
        verify(fileStorageService).ensureBucketExists("project-documents");
        verify(fileStorageService).upload(eq("project-documents"), anyString(), any(InputStream.class), eq("application/pdf"), eq(1024L));
        verify(eventPublisher).publish(eq("etsopy.project"), eq("project.document_uploaded"), any());
    }

    @Test
    void downloadDocument_ReturnsResource() {
        UUID documentId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        Project project = Project.builder().id(projectId).title("P").status(ProjectStatus.ACTIVE).createdBy(UUID.randomUUID()).build();
        ProjectDocument document = ProjectDocument.builder()
                .id(documentId)
                .project(project)
                .filePath("some/path/file.pdf")
                .version(1)
                .uploadedBy(UUID.randomUUID())
                .build();

        when(projectDocumentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(fileStorageService.download("project-documents", "some/path/file.pdf"))
                .thenReturn(new ByteArrayInputStream(new byte[10]));

        ProjectDocumentService.DocumentDownload result = projectDocumentService.downloadDocument(documentId);

        assertNotNull(result);
        assertNotNull(result.resource());
        assertEquals("file.pdf", result.filename());
        verify(fileStorageService).download("project-documents", "some/path/file.pdf");
    }

    @Test
    void downloadDocument_WhenNotFound_ThrowsException() {
        UUID documentId = UUID.randomUUID();
        when(projectDocumentRepository.findById(documentId)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class,
                () -> projectDocumentService.downloadDocument(documentId));
    }

    @Test
    void getDocumentsByProjectId_ReturnsList() {
        UUID projectId = UUID.randomUUID();
        Project project = Project.builder().id(projectId).title("P").status(ProjectStatus.ACTIVE).createdBy(UUID.randomUUID()).build();
        ProjectDocument doc1 = ProjectDocument.builder()
                .id(UUID.randomUUID())
                .project(project)
                .filePath("path1")
                .version(1)
                .uploadedBy(UUID.randomUUID())
                .build();
        ProjectDocument doc2 = ProjectDocument.builder()
                .id(UUID.randomUUID())
                .project(project)
                .filePath("path2")
                .version(1)
                .uploadedBy(UUID.randomUUID())
                .build();

        when(projectDocumentRepository.findByProjectId(projectId)).thenReturn(List.of(doc1, doc2));

        List<ProjectDocumentDto> result = projectDocumentService.getDocumentsByProjectId(projectId);

        assertEquals(2, result.size());
    }

    @Test
    void deleteDocument_DeletesFromStorageAndRepository() {
        UUID documentId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        Project project = Project.builder().id(projectId).title("P").status(ProjectStatus.ACTIVE).createdBy(UUID.randomUUID()).build();
        ProjectDocument document = ProjectDocument.builder()
                .id(documentId)
                .project(project)
                .filePath("some/path/file.pdf")
                .version(1)
                .uploadedBy(UUID.randomUUID())
                .build();

        when(projectDocumentRepository.findById(documentId)).thenReturn(Optional.of(document));

        projectDocumentService.deleteDocument(documentId);

        verify(fileStorageService).delete("project-documents", "some/path/file.pdf");
        verify(projectDocumentRepository).deleteById(documentId);
    }

    @Test
    void deleteDocument_WhenNotFound_ThrowsException() {
        UUID documentId = UUID.randomUUID();
        when(projectDocumentRepository.findById(documentId)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class,
                () -> projectDocumentService.deleteDocument(documentId));
    }

    @Test
    void getDocumentById_WhenNotFound_ThrowsException() {
        UUID documentId = UUID.randomUUID();
        when(projectDocumentRepository.findById(documentId)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class,
                () -> projectDocumentService.getDocumentById(documentId));
    }

    @Test
    void getDocumentById_WhenFound_ReturnsDto() {
        UUID documentId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        Project project = Project.builder().id(projectId).title("P").status(ProjectStatus.ACTIVE).createdBy(UUID.randomUUID()).build();
        ProjectDocument document = ProjectDocument.builder()
                .id(documentId)
                .project(project)
                .filePath("path/file.pdf")
                .version(1)
                .uploadedBy(UUID.randomUUID())
                .description("A document")
                .build();

        when(projectDocumentRepository.findById(documentId)).thenReturn(Optional.of(document));

        ProjectDocumentDto result = projectDocumentService.getDocumentById(documentId);

        assertEquals(documentId, result.getId());
        assertEquals(projectId, result.getProjectId());
        assertEquals("A document", result.getDescription());
    }
}
