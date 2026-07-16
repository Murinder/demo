package com.example.documentservice.service;

import com.example.documentservice.dto.GenerateDocumentRequest;
import com.example.documentservice.dto.GeneratedDocumentDto;
import com.example.documentservice.model.entity.DocumentTemplate;
import com.example.documentservice.model.entity.GeneratedDocument;
import com.example.documentservice.repository.GeneratedDocumentRepository;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.sharedlib.event.DocumentEvent;
import com.example.sharedlib.event.EventPublisher;
import com.example.sharedlib.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentGenerationService {

    private static final String BUCKET = "generated-documents";

    private final GeneratedDocumentRepository generatedDocumentRepository;
    private final DocumentTemplateService templateService;
    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public GeneratedDocumentDto generateDocument(GenerateDocumentRequest request, UUID generatedBy) {
        log.info("Generating document from template: {}, for user: {}", request.getTemplateId(), request.getGeneratedFor());

        DocumentTemplate template = templateService.findTemplateOrThrow(request.getTemplateId());

        String parametersJson = "{}";
        if (request.getParameters() != null && !request.getParameters().isEmpty()) {
            try {
                parametersJson = objectMapper.writeValueAsString(request.getParameters());
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize parameters", e);
                parametersJson = "{}";
            }
        }

        String filePath = BUCKET + "/" + UUID.randomUUID() + "/" + template.getName().replaceAll("\\s+", "_") + ".pdf";

        GeneratedDocument document = GeneratedDocument.builder()
                .templateId(template.getId())
                .generatedFor(request.getGeneratedFor())
                .filePath(filePath)
                .generatedBy(generatedBy)
                .parameters(parametersJson)
                .status("COMPLETED")
                .build();

        document = generatedDocumentRepository.save(document);
        log.info("Generated document: id={}, status={}", document.getId(), document.getStatus());

        publishDocumentGeneratedEvent(document, template);

        return toDto(document, template.getName());
    }

    @Transactional(readOnly = true)
    public GeneratedDocumentDto getDocumentById(UUID documentId) {
        GeneratedDocument document = findDocumentOrThrow(documentId);
        String templateName = null;
        try {
            templateName = templateService.getTemplateById(document.getTemplateId()).getName();
        } catch (Exception e) {
            log.warn("Could not fetch template name for document: {}", documentId);
        }
        return toDto(document, templateName);
    }

    public GeneratedDocument findDocumentOrThrow(UUID documentId) {
        return generatedDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("GeneratedDocument", "id", documentId));
    }

    private void publishDocumentGeneratedEvent(GeneratedDocument document, DocumentTemplate template) {
        try {
            DocumentEvent event = DocumentEvent.builder()
                    .documentId(document.getId())
                    .templateId(template.getId())
                    .generatedFor(document.getGeneratedFor())
                    .generatedBy(document.getGeneratedBy())
                    .documentType(template.getDocumentType().name())
                    .templateName(template.getName())
                    .build();
            event.init("document-service");

            eventPublisher.publish(
                    RabbitMqAutoConfiguration.DOCUMENT_EXCHANGE,
                    RabbitMqAutoConfiguration.DOCUMENT_GENERATED_KEY,
                    event
            );
            log.info("Published document.generated event for document: {}", document.getId());
        } catch (Exception e) {
            log.error("Failed to publish document.generated event for document: {}", document.getId(), e);
        }
    }

    private GeneratedDocumentDto toDto(GeneratedDocument entity, String templateName) {
        return GeneratedDocumentDto.builder()
                .id(entity.getId())
                .templateId(entity.getTemplateId())
                .templateName(templateName)
                .generatedFor(entity.getGeneratedFor())
                .filePath(entity.getFilePath())
                .generatedAt(entity.getGeneratedAt())
                .generatedBy(entity.getGeneratedBy())
                .parameters(entity.getParameters())
                .status(entity.getStatus())
                .errorMessage(entity.getErrorMessage())
                .signedAt(entity.getSignedAt())
                .expiresAt(entity.getExpiresAt())
                .build();
    }
}
