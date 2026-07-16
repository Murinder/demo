package com.example.documentservice.service;

import com.example.documentservice.dto.DocumentSignatureDto;
import com.example.documentservice.model.entity.DocumentSignature;
import com.example.documentservice.model.entity.GeneratedDocument;
import com.example.documentservice.repository.DocumentSignatureRepository;
import com.example.documentservice.repository.GeneratedDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentSignatureService {

    private final DocumentSignatureRepository signatureRepository;
    private final DocumentGenerationService documentGenerationService;
    private final GeneratedDocumentRepository generatedDocumentRepository;

    @Transactional
    public DocumentSignatureDto signDocument(UUID documentId, UUID signerId, String signatureType,
                                              String ipAddress, String userAgent, String comment) {
        log.info("Signing document: documentId={}, signerId={}", documentId, signerId);

        GeneratedDocument document = documentGenerationService.findDocumentOrThrow(documentId);

        DocumentSignature signature = DocumentSignature.builder()
                .id(DocumentSignature.DocumentSignatureId.builder()
                        .documentId(documentId)
                        .signerId(signerId)
                        .build())
                .signedAt(OffsetDateTime.now())
                .signatureType(signatureType != null ? signatureType : "ELECTRONIC")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .comment(comment)
                .build();

        signature = signatureRepository.save(signature);

        document.setSignedAt(OffsetDateTime.now());
        generatedDocumentRepository.save(document);

        log.info("Document signed: documentId={}, signerId={}", documentId, signerId);
        return toDto(signature);
    }

    @Transactional(readOnly = true)
    public List<DocumentSignatureDto> getSignaturesByDocumentId(UUID documentId) {
        return signatureRepository.findByIdDocumentId(documentId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private DocumentSignatureDto toDto(DocumentSignature entity) {
        return DocumentSignatureDto.builder()
                .documentId(entity.getId().getDocumentId())
                .signerId(entity.getId().getSignerId())
                .signedAt(entity.getSignedAt())
                .signatureType(entity.getSignatureType())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .comment(entity.getComment())
                .build();
    }
}
