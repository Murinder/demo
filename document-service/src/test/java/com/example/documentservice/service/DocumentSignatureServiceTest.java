package com.example.documentservice.service;

import com.example.documentservice.dto.DocumentSignatureDto;
import com.example.documentservice.model.entity.DocumentSignature;
import com.example.documentservice.model.entity.GeneratedDocument;
import com.example.documentservice.repository.DocumentSignatureRepository;
import com.example.documentservice.repository.GeneratedDocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentSignatureServiceTest {

    @Mock
    private DocumentSignatureRepository signatureRepository;

    @Mock
    private DocumentGenerationService documentGenerationService;

    @Mock
    private GeneratedDocumentRepository generatedDocumentRepository;

    @InjectMocks
    private DocumentSignatureService documentSignatureService;

    @Test
    void signDocument_shouldCreateSignatureAndUpdateDocument() {
        UUID documentId = UUID.randomUUID();
        UUID signerId = UUID.randomUUID();

        GeneratedDocument document = GeneratedDocument.builder()
                .id(documentId)
                .templateId(UUID.randomUUID())
                .generatedFor(UUID.randomUUID())
                .filePath("generated-documents/file.pdf")
                .generatedBy(UUID.randomUUID())
                .generatedAt(OffsetDateTime.now())
                .parameters("{}")
                .status("COMPLETED")
                .build();

        DocumentSignature savedSignature = DocumentSignature.builder()
                .id(DocumentSignature.DocumentSignatureId.builder()
                        .documentId(documentId)
                        .signerId(signerId)
                        .build())
                .signedAt(OffsetDateTime.now())
                .signatureType("ELECTRONIC")
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .comment("Approved")
                .build();

        when(documentGenerationService.findDocumentOrThrow(documentId)).thenReturn(document);
        when(signatureRepository.save(any(DocumentSignature.class))).thenReturn(savedSignature);
        when(generatedDocumentRepository.save(any(GeneratedDocument.class))).thenReturn(document);

        DocumentSignatureDto result = documentSignatureService.signDocument(
                documentId, signerId, "ELECTRONIC", "192.168.1.1", "Mozilla/5.0", "Approved");

        assertThat(result).isNotNull();
        assertThat(result.getDocumentId()).isEqualTo(documentId);
        assertThat(result.getSignerId()).isEqualTo(signerId);
        assertThat(result.getSignatureType()).isEqualTo("ELECTRONIC");
        assertThat(result.getIpAddress()).isEqualTo("192.168.1.1");
        assertThat(result.getComment()).isEqualTo("Approved");
        verify(signatureRepository).save(any(DocumentSignature.class));
        verify(generatedDocumentRepository).save(any(GeneratedDocument.class));
    }

    @Test
    void signDocument_withNullSignatureType_shouldDefaultToElectronic() {
        UUID documentId = UUID.randomUUID();
        UUID signerId = UUID.randomUUID();

        GeneratedDocument document = GeneratedDocument.builder()
                .id(documentId)
                .templateId(UUID.randomUUID())
                .generatedFor(UUID.randomUUID())
                .filePath("generated-documents/file.pdf")
                .generatedBy(UUID.randomUUID())
                .generatedAt(OffsetDateTime.now())
                .parameters("{}")
                .status("COMPLETED")
                .build();

        DocumentSignature savedSignature = DocumentSignature.builder()
                .id(DocumentSignature.DocumentSignatureId.builder()
                        .documentId(documentId)
                        .signerId(signerId)
                        .build())
                .signedAt(OffsetDateTime.now())
                .signatureType("ELECTRONIC")
                .build();

        when(documentGenerationService.findDocumentOrThrow(documentId)).thenReturn(document);
        when(signatureRepository.save(any(DocumentSignature.class))).thenReturn(savedSignature);
        when(generatedDocumentRepository.save(any(GeneratedDocument.class))).thenReturn(document);

        DocumentSignatureDto result = documentSignatureService.signDocument(
                documentId, signerId, null, null, null, null);

        assertThat(result.getSignatureType()).isEqualTo("ELECTRONIC");
    }

    @Test
    void signDocument_shouldUpdateDocumentSignedAt() {
        UUID documentId = UUID.randomUUID();
        UUID signerId = UUID.randomUUID();

        GeneratedDocument document = GeneratedDocument.builder()
                .id(documentId)
                .templateId(UUID.randomUUID())
                .generatedFor(UUID.randomUUID())
                .filePath("generated-documents/file.pdf")
                .generatedBy(UUID.randomUUID())
                .generatedAt(OffsetDateTime.now())
                .parameters("{}")
                .status("COMPLETED")
                .signedAt(null)
                .build();

        DocumentSignature savedSignature = DocumentSignature.builder()
                .id(DocumentSignature.DocumentSignatureId.builder()
                        .documentId(documentId)
                        .signerId(signerId)
                        .build())
                .signedAt(OffsetDateTime.now())
                .signatureType("DIGITAL")
                .build();

        when(documentGenerationService.findDocumentOrThrow(documentId)).thenReturn(document);
        when(signatureRepository.save(any(DocumentSignature.class))).thenReturn(savedSignature);
        when(generatedDocumentRepository.save(any(GeneratedDocument.class))).thenReturn(document);

        documentSignatureService.signDocument(
                documentId, signerId, "DIGITAL", "10.0.0.1", "Chrome", "Signed");

        verify(generatedDocumentRepository).save(argThat(doc -> doc.getSignedAt() != null));
    }

    @Test
    void getSignaturesByDocumentId_shouldReturnSignatures() {
        UUID documentId = UUID.randomUUID();
        UUID signer1 = UUID.randomUUID();
        UUID signer2 = UUID.randomUUID();

        DocumentSignature sig1 = DocumentSignature.builder()
                .id(DocumentSignature.DocumentSignatureId.builder()
                        .documentId(documentId)
                        .signerId(signer1)
                        .build())
                .signedAt(OffsetDateTime.now().minusHours(2))
                .signatureType("ELECTRONIC")
                .ipAddress("10.0.0.1")
                .build();

        DocumentSignature sig2 = DocumentSignature.builder()
                .id(DocumentSignature.DocumentSignatureId.builder()
                        .documentId(documentId)
                        .signerId(signer2)
                        .build())
                .signedAt(OffsetDateTime.now())
                .signatureType("DIGITAL")
                .ipAddress("10.0.0.2")
                .build();

        when(signatureRepository.findByIdDocumentId(documentId)).thenReturn(List.of(sig1, sig2));

        List<DocumentSignatureDto> results = documentSignatureService.getSignaturesByDocumentId(documentId);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(DocumentSignatureDto::getSignerId)
                .containsExactlyInAnyOrder(signer1, signer2);
        verify(signatureRepository).findByIdDocumentId(documentId);
    }

    @Test
    void getSignaturesByDocumentId_whenNoSignatures_shouldReturnEmptyList() {
        UUID documentId = UUID.randomUUID();
        when(signatureRepository.findByIdDocumentId(documentId)).thenReturn(List.of());

        List<DocumentSignatureDto> results = documentSignatureService.getSignaturesByDocumentId(documentId);

        assertThat(results).isEmpty();
    }
}
