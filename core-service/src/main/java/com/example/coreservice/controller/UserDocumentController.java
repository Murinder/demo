package com.example.coreservice.controller;

import com.example.coreservice.dto.UserDocumentDto;
import com.example.coreservice.service.UserDocumentService;
import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/user-documents")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "User Documents", description = "Personal document management (diplomas, certificates)")
public class UserDocumentController {

    private final UserDocumentService userDocumentService;

    @PostMapping("/upload")
    @Operation(summary = "Upload personal document")
    public ResponseEntity<ApiResponse<UserDocumentDto>> uploadDocument(
            @RequestParam("userId") UUID userId,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file) {
        log.info("Uploading personal document for user {}: {}", userId, file.getOriginalFilename());
        UserDocumentDto dto = userDocumentService.uploadDocument(userId, description, file);
        return ResponseEntity.ok(ApiResponse.<UserDocumentDto>builder()
                .success(true)
                .code("USER_DOCUMENT_UPLOADED")
                .message("Document uploaded")
                .data(dto)
                .build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "List user's personal documents")
    public ResponseEntity<ApiResponse<List<UserDocumentDto>>> getUserDocuments(@PathVariable UUID userId) {
        List<UserDocumentDto> docs = userDocumentService.getDocumentsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.<List<UserDocumentDto>>builder()
                .success(true)
                .code("USER_DOCUMENTS_RETRIEVED")
                .data(docs)
                .build());
    }

    @GetMapping("/download/{documentId}")
    @Operation(summary = "Download personal document")
    public ResponseEntity<Resource> downloadDocument(@PathVariable UUID documentId) {
        UserDocumentService.DocumentDownload download = userDocumentService.downloadDocument(documentId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + download.filename() + "\"")
                .body(download.resource());
    }

    @DeleteMapping("/{documentId}")
    @Operation(summary = "Delete personal document")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable UUID documentId) {
        userDocumentService.deleteDocument(documentId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .code("USER_DOCUMENT_DELETED")
                .message("Document deleted")
                .build());
    }
}
