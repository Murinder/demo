package com.example.documentservice.service;

import com.example.documentservice.dto.GenerateDocumentRequest;
import com.example.documentservice.dto.GeneratedDocumentDto;
import com.example.documentservice.model.entity.DocumentTemplate;
import com.example.documentservice.model.entity.GeneratedDocument;
import com.example.documentservice.model.enums.DocumentType;
import com.example.documentservice.model.enums.TemplateStatus;
import com.example.documentservice.repository.GeneratedDocumentRepository;
import com.example.sharedlib.event.EventPublisher;
import com.example.sharedlib.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentGenerationServiceTest {

    @Mock
    private GeneratedDocumentRepository generatedDocumentRepository;

    @Mock
    private DocumentTemplateService templateService;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DocumentGenerationService documentGenerationService;

    private UUID templateId;
    private UUID generatedFor;
    private UUID generatedBy;
    private DocumentTemplate template;

    @BeforeEach
    void setUp() {
        templateId = UUID.randomUUID();
        generatedFor = UUID.randomUUID();
        generatedBy = UUID.randomUUID();

        template = DocumentTemplate.builder()
                .id(templateId)
                .name("Certificate Template")
                .description("A test template")
                .filePath("document-templates/" + UUID.randomUUID() + "/template")
                .documentType(DocumentType.CERTIFICATE)
                .createdBy(UUID.randomUUID())
                .status(TemplateStatus.DRAFT)
                .version(1)
                .isPublic(false)
                .build();
    }

    @Test
    void generateDocument_shouldCreateDocumentAndPublishEvent() throws Exception {
        GenerateDocumentRequest request = GenerateDocumentRequest.builder()
                .templateId(templateId)
                .generatedFor(generatedFor)
                .parameters(Map.of("studentName", "John Doe"))
                .build();

        UUID documentId = UUID.randomUUID();
        GeneratedDocument saved = GeneratedDocument.builder()
                .id(documentId)
                .templateId(templateId)
                .generatedFor(generatedFor)
                .filePath("generated-documents/" + UUID.randomUUID() + "/Certificate_Template.pdf")
                .generatedBy(generatedBy)
                .generatedAt(OffsetDateTime.now())
                .parameters("{\"studentName\":\"John Doe\"}")
                .status("COMPLETED")
                .build();

        when(templateService.findTemplateOrThrow(templateId)).thenReturn(template);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"studentName\":\"John Doe\"}");
        when(generatedDocumentRepository.save(any(GeneratedDocument.class))).thenReturn(saved);

        GeneratedDocumentDto result = documentGenerationService.generateDocument(request, generatedBy);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(documentId);
        assertThat(result.getTemplateId()).isEqualTo(templateId);
        assertThat(result.getGeneratedFor()).isEqualTo(generatedFor);
        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        assertThat(result.getTemplateName()).isEqualTo("Certificate Template");
        verify(templateService).findTemplateOrThrow(templateId);
        verify(generatedDocumentRepository).save(any(GeneratedDocument.class));
        verify(eventPublisher).publish(anyString(), anyString(), any());
    }

    @Test
    void generateDocument_withNullParameters_shouldUseEmptyJson() throws Exception {
        GenerateDocumentRequest request = GenerateDocumentRequest.builder()
                .templateId(templateId)
                .generatedFor(generatedFor)
                .parameters(null)
                .build();

        UUID documentId = UUID.randomUUID();
        GeneratedDocument saved = GeneratedDocument.builder()
                .id(documentId)
                .templateId(templateId)
                .generatedFor(generatedFor)
                .filePath("generated-documents/" + UUID.randomUUID() + "/Certificate_Template.pdf")
                .generatedBy(generatedBy)
                .generatedAt(OffsetDateTime.now())
                .parameters("{}")
                .status("COMPLETED")
                .build();

        when(templateService.findTemplateOrThrow(templateId)).thenReturn(template);
        when(generatedDocumentRepository.save(any(GeneratedDocument.class))).thenReturn(saved);

        GeneratedDocumentDto result = documentGenerationService.generateDocument(request, generatedBy);

        assertThat(result).isNotNull();
        assertThat(result.getParameters()).isEqualTo("{}");
        verify(objectMapper, never()).writeValueAsString(any());
    }

    @Test
    void generateDocument_withEmptyParameters_shouldUseEmptyJson() throws Exception {
        GenerateDocumentRequest request = GenerateDocumentRequest.builder()
                .templateId(templateId)
                .generatedFor(generatedFor)
                .parameters(Map.of())
                .build();

        UUID documentId = UUID.randomUUID();
        GeneratedDocument saved = GeneratedDocument.builder()
                .id(documentId)
                .templateId(templateId)
                .generatedFor(generatedFor)
                .filePath("generated-documents/" + UUID.randomUUID() + "/Certificate_Template.pdf")
                .generatedBy(generatedBy)
                .generatedAt(OffsetDateTime.now())
                .parameters("{}")
                .status("COMPLETED")
                .build();

        when(templateService.findTemplateOrThrow(templateId)).thenReturn(template);
        when(generatedDocumentRepository.save(any(GeneratedDocument.class))).thenReturn(saved);

        GeneratedDocumentDto result = documentGenerationService.generateDocument(request, generatedBy);

        assertThat(result).isNotNull();
        verify(objectMapper, never()).writeValueAsString(any());
    }

    @Test
    void getDocumentById_shouldReturnDocument() {
        UUID documentId = UUID.randomUUID();
        GeneratedDocument document = GeneratedDocument.builder()
                .id(documentId)
                .templateId(templateId)
                .generatedFor(generatedFor)
                .filePath("generated-documents/file.pdf")
                .generatedBy(generatedBy)
                .generatedAt(OffsetDateTime.now())
                .parameters("{}")
                .status("COMPLETED")
                .build();

        com.example.documentservice.dto.DocumentTemplateDto templateDto =
                com.example.documentservice.dto.DocumentTemplateDto.builder()
                        .id(templateId)
                        .name("Certificate Template")
                        .build();

        when(generatedDocumentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(templateService.getTemplateById(templateId)).thenReturn(templateDto);

        GeneratedDocumentDto result = documentGenerationService.getDocumentById(documentId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(documentId);
        assertThat(result.getTemplateName()).isEqualTo("Certificate Template");
    }

    @Test
    void getDocumentById_whenNotFound_shouldThrow() {
        UUID documentId = UUID.randomUUID();
        when(generatedDocumentRepository.findById(documentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentGenerationService.getDocumentById(documentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getDocumentById_whenTemplateNotFound_shouldStillReturnDocument() {
        UUID documentId = UUID.randomUUID();
        GeneratedDocument document = GeneratedDocument.builder()
                .id(documentId)
                .templateId(templateId)
                .generatedFor(generatedFor)
                .filePath("generated-documents/file.pdf")
                .generatedBy(generatedBy)
                .generatedAt(OffsetDateTime.now())
                .parameters("{}")
                .status("COMPLETED")
                .build();

        when(generatedDocumentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(templateService.getTemplateById(templateId))
                .thenThrow(new ResourceNotFoundException("DocumentTemplate", "id", templateId));

        GeneratedDocumentDto result = documentGenerationService.getDocumentById(documentId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(documentId);
        assertThat(result.getTemplateName()).isNull();
    }
}
