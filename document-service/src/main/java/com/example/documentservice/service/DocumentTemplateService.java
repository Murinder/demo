package com.example.documentservice.service;

import com.example.documentservice.dto.CreateTemplateDto;
import com.example.documentservice.dto.DocumentTemplateDto;
import com.example.documentservice.model.entity.DocumentTemplate;
import com.example.documentservice.model.enums.TemplateStatus;
import com.example.documentservice.repository.DocumentTemplateRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import com.example.sharedlib.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentTemplateService {

    private static final String BUCKET = "document-templates";

    private final DocumentTemplateRepository templateRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public DocumentTemplateDto createTemplate(CreateTemplateDto dto, UUID createdBy) {
        log.info("Creating document template: name={}, type={}", dto.getName(), dto.getDocumentType());

        fileStorageService.ensureBucketExists(BUCKET);

        String filePath = BUCKET + "/" + UUID.randomUUID() + "/template";

        DocumentTemplate template = DocumentTemplate.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .filePath(filePath)
                .documentType(dto.getDocumentType())
                .createdBy(createdBy)
                .status(TemplateStatus.DRAFT)
                .version(1)
                .isPublic(dto.getIsPublic() != null ? dto.getIsPublic() : false)
                .facultyId(dto.getFacultyId())
                .departmentId(dto.getDepartmentId())
                .build();

        template = templateRepository.save(template);
        log.info("Created document template: id={}", template.getId());
        return toDto(template);
    }

    @Transactional(readOnly = true)
    public List<DocumentTemplateDto> getAllTemplates() {
        return templateRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DocumentTemplateDto getTemplateById(UUID templateId) {
        DocumentTemplate template = findTemplateOrThrow(templateId);
        return toDto(template);
    }

    @Transactional
    public DocumentTemplateDto updateTemplate(UUID templateId, CreateTemplateDto dto) {
        DocumentTemplate template = findTemplateOrThrow(templateId);

        template.setName(dto.getName());
        template.setDescription(dto.getDescription());
        template.setDocumentType(dto.getDocumentType());
        template.setIsPublic(dto.getIsPublic());
        template.setFacultyId(dto.getFacultyId());
        template.setDepartmentId(dto.getDepartmentId());
        template.setVersion(template.getVersion() + 1);

        template = templateRepository.save(template);
        log.info("Updated document template: id={}, version={}", template.getId(), template.getVersion());
        return toDto(template);
    }

    @Transactional
    public void deleteTemplate(UUID templateId) {
        DocumentTemplate template = findTemplateOrThrow(templateId);
        templateRepository.delete(template);
        log.info("Deleted document template: id={}", templateId);
    }

    public DocumentTemplate findTemplateOrThrow(UUID templateId) {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("DocumentTemplate", "id", templateId));
    }

    private DocumentTemplateDto toDto(DocumentTemplate entity) {
        return DocumentTemplateDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .filePath(entity.getFilePath())
                .documentType(entity.getDocumentType())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .status(entity.getStatus())
                .version(entity.getVersion())
                .isPublic(entity.getIsPublic())
                .facultyId(entity.getFacultyId())
                .departmentId(entity.getDepartmentId())
                .build();
    }
}
