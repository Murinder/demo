package com.example.documentservice.service;

import com.example.documentservice.dto.CreateTemplateDto;
import com.example.documentservice.dto.DocumentTemplateDto;
import com.example.documentservice.model.entity.DocumentTemplate;
import com.example.documentservice.model.enums.DocumentType;
import com.example.documentservice.model.enums.TemplateStatus;
import com.example.documentservice.repository.DocumentTemplateRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import com.example.sharedlib.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentTemplateServiceTest {

    @Mock
    private DocumentTemplateRepository templateRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private DocumentTemplateService templateService;

    private UUID templateId;
    private UUID createdBy;
    private DocumentTemplate template;

    @BeforeEach
    void setUp() {
        templateId = UUID.randomUUID();
        createdBy = UUID.randomUUID();

        template = DocumentTemplate.builder()
                .id(templateId)
                .name("Test Certificate Template")
                .description("A test template for certificates")
                .filePath("document-templates/" + UUID.randomUUID() + "/template")
                .documentType(DocumentType.CERTIFICATE)
                .createdBy(createdBy)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .status(TemplateStatus.DRAFT)
                .version(1)
                .isPublic(false)
                .build();
    }

    @Test
    void createTemplate_shouldReturnCreatedTemplate() {
        CreateTemplateDto dto = CreateTemplateDto.builder()
                .name("Test Certificate Template")
                .description("A test template for certificates")
                .documentType(DocumentType.CERTIFICATE)
                .isPublic(false)
                .build();

        doNothing().when(fileStorageService).ensureBucketExists(anyString());
        when(templateRepository.save(any(DocumentTemplate.class))).thenReturn(template);

        DocumentTemplateDto result = templateService.createTemplate(dto, createdBy);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(templateId);
        assertThat(result.getName()).isEqualTo("Test Certificate Template");
        assertThat(result.getDocumentType()).isEqualTo(DocumentType.CERTIFICATE);
        assertThat(result.getStatus()).isEqualTo(TemplateStatus.DRAFT);
        verify(fileStorageService).ensureBucketExists("document-templates");
        verify(templateRepository).save(any(DocumentTemplate.class));
    }

    @Test
    void getAllTemplates_shouldReturnList() {
        when(templateRepository.findAll()).thenReturn(List.of(template));

        List<DocumentTemplateDto> result = templateService.getAllTemplates();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Certificate Template");
    }

    @Test
    void getTemplateById_shouldReturnTemplate() {
        when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));

        DocumentTemplateDto result = templateService.getTemplateById(templateId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(templateId);
    }

    @Test
    void getTemplateById_notFound_shouldThrow() {
        UUID nonExistentId = UUID.randomUUID();
        when(templateRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> templateService.getTemplateById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateTemplate_shouldReturnUpdatedTemplate() {
        CreateTemplateDto dto = CreateTemplateDto.builder()
                .name("Updated Template")
                .description("Updated description")
                .documentType(DocumentType.REPORT)
                .isPublic(true)
                .build();

        DocumentTemplate updatedTemplate = DocumentTemplate.builder()
                .id(templateId)
                .name("Updated Template")
                .description("Updated description")
                .filePath(template.getFilePath())
                .documentType(DocumentType.REPORT)
                .createdBy(createdBy)
                .createdAt(template.getCreatedAt())
                .updatedAt(OffsetDateTime.now())
                .status(TemplateStatus.DRAFT)
                .version(2)
                .isPublic(true)
                .build();

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));
        when(templateRepository.save(any(DocumentTemplate.class))).thenReturn(updatedTemplate);

        DocumentTemplateDto result = templateService.updateTemplate(templateId, dto);

        assertThat(result.getName()).isEqualTo("Updated Template");
        assertThat(result.getVersion()).isEqualTo(2);
        assertThat(result.getDocumentType()).isEqualTo(DocumentType.REPORT);
    }

    @Test
    void deleteTemplate_shouldDeleteSuccessfully() {
        when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));
        doNothing().when(templateRepository).delete(template);

        templateService.deleteTemplate(templateId);

        verify(templateRepository).delete(template);
    }

    @Test
    void deleteTemplate_notFound_shouldThrow() {
        UUID nonExistentId = UUID.randomUUID();
        when(templateRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> templateService.deleteTemplate(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createTemplate_withNullIsPublic_shouldDefaultToFalse() {
        CreateTemplateDto dto = CreateTemplateDto.builder()
                .name("Diploma Template")
                .description("A diploma template")
                .documentType(DocumentType.DIPLOMA)
                .isPublic(null)
                .build();

        DocumentTemplate savedTemplate = DocumentTemplate.builder()
                .id(UUID.randomUUID())
                .name("Diploma Template")
                .description("A diploma template")
                .filePath("document-templates/" + UUID.randomUUID() + "/template")
                .documentType(DocumentType.DIPLOMA)
                .createdBy(createdBy)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .status(TemplateStatus.DRAFT)
                .version(1)
                .isPublic(false)
                .build();

        doNothing().when(fileStorageService).ensureBucketExists(anyString());
        when(templateRepository.save(any(DocumentTemplate.class))).thenReturn(savedTemplate);

        DocumentTemplateDto result = templateService.createTemplate(dto, createdBy);

        assertThat(result.getIsPublic()).isFalse();
        assertThat(result.getVersion()).isEqualTo(1);
        assertThat(result.getStatus()).isEqualTo(TemplateStatus.DRAFT);
    }

    @Test
    void updateTemplate_shouldIncrementVersion() {
        CreateTemplateDto dto = CreateTemplateDto.builder()
                .name("Updated Name")
                .description("Updated desc")
                .documentType(DocumentType.AGREEMENT)
                .isPublic(true)
                .build();

        DocumentTemplate updatedTemplate = DocumentTemplate.builder()
                .id(templateId)
                .name("Updated Name")
                .description("Updated desc")
                .filePath(template.getFilePath())
                .documentType(DocumentType.AGREEMENT)
                .createdBy(createdBy)
                .createdAt(template.getCreatedAt())
                .updatedAt(OffsetDateTime.now())
                .status(TemplateStatus.DRAFT)
                .version(2)
                .isPublic(true)
                .build();

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));
        when(templateRepository.save(any(DocumentTemplate.class))).thenReturn(updatedTemplate);

        DocumentTemplateDto result = templateService.updateTemplate(templateId, dto);

        assertThat(result.getVersion()).isEqualTo(2);
        assertThat(result.getIsPublic()).isTrue();
        assertThat(result.getDocumentType()).isEqualTo(DocumentType.AGREEMENT);
    }

    @Test
    void getAllTemplates_whenEmpty_shouldReturnEmptyList() {
        when(templateRepository.findAll()).thenReturn(List.of());

        List<DocumentTemplateDto> result = templateService.getAllTemplates();

        assertThat(result).isEmpty();
        verify(templateRepository).findAll();
    }
}
