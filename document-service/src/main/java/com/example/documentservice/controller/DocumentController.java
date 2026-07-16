package com.example.documentservice.controller;

import com.example.documentservice.dto.DocumentSignatureDto;
import com.example.documentservice.dto.GenerateDocumentRequest;
import com.example.documentservice.dto.GeneratedDocumentDto;
import com.example.documentservice.service.DocumentGenerationService;
import com.example.documentservice.service.DocumentSignatureService;
import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.security.AuthenticatedOnly;
import com.example.sharedlib.security.LecturerOrAbove;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.sharedlib.storage.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Documents", description = "Генерация и управление документами")
public class DocumentController {

    private static final String BUCKET = "generated-documents";

    private final DocumentGenerationService generationService;
    private final DocumentSignatureService signatureService;
    private final FileStorageService fileStorageService;

    @Operation(summary = "Generate a new document from template")
    @PostMapping("/generate")
    @LecturerOrAbove
    public ResponseEntity<ApiResponse<GeneratedDocumentDto>> generateDocument(
            @Valid @RequestBody GenerateDocumentRequest request,
            Authentication authentication) {
        UUID generatedBy = UUID.fromString(authentication.getName());
        GeneratedDocumentDto result = generationService.generateDocument(request, generatedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result, "Document generated successfully"));
    }

    @Operation(summary = "Get a generated document by ID")
    @GetMapping("/{documentId}")
    public ResponseEntity<ApiResponse<GeneratedDocumentDto>> getDocument(@PathVariable UUID documentId) {
        GeneratedDocumentDto document = generationService.getDocumentById(documentId);
        return ResponseEntity.ok(ApiResponse.success(document));
    }

    @Operation(summary = "Download a generated document as PDF")
    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable UUID documentId) {
        GeneratedDocumentDto document = generationService.getDocumentById(documentId);
        String objectName = document.getFilePath().replace(BUCKET + "/", "");

        InputStream inputStream = fileStorageService.download(BUCKET, objectName);
        InputStreamResource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @Operation(summary = "Sign a document electronically")
    @PostMapping("/{documentId}/sign")
    public ResponseEntity<ApiResponse<DocumentSignatureDto>> signDocument(
            @PathVariable UUID documentId,
            @RequestParam(required = false, defaultValue = "ELECTRONIC") String signatureType,
            @RequestParam(required = false) String comment,
            Authentication authentication,
            HttpServletRequest request) {
        UUID signerId = UUID.fromString(authentication.getName());
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        DocumentSignatureDto result = signatureService.signDocument(
                documentId, signerId, signatureType, ipAddress, userAgent, comment);
        return ResponseEntity.ok(ApiResponse.success(result, "Document signed successfully"));
    }
}
