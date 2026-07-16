package com.example.projectservice.controller;

import com.example.projectservice.dto.ProjectDocumentDto;
import com.example.projectservice.service.ProjectDocumentService;
import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.security.AuthenticatedOnly;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@AuthenticatedOnly
public class ProjectDocumentController {

    private final ProjectDocumentService projectDocumentService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<ProjectDocumentDto>> uploadDocument(
            @RequestParam("projectId") UUID projectId,
            @RequestParam("userId") UUID userId,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file) {
        ProjectDocumentDto documentDto = projectDocumentService.uploadDocument(projectId, userId, description, file);
        return ResponseEntity.ok(ApiResponse.<ProjectDocumentDto>builder()
                .success(true).code("DOCUMENT_UPLOADED").message("Document uploaded").data(documentDto).build());
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable UUID documentId) {
        ProjectDocumentService.DocumentDownload download = projectDocumentService.downloadDocument(documentId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + download.filename() + "\"")
                .body(download.resource());
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<ApiResponse<ProjectDocumentDto>> getDocumentById(@PathVariable UUID documentId) {
        ProjectDocumentDto documentDto = projectDocumentService.getDocumentById(documentId);
        return ResponseEntity.ok(ApiResponse.<ProjectDocumentDto>builder()
                .success(true).code("DOCUMENT_RETRIEVED").data(documentDto).build());
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<ProjectDocumentDto>>> getDocumentsByProjectId(@PathVariable UUID projectId) {
        List<ProjectDocumentDto> documents = projectDocumentService.getDocumentsByProjectId(projectId);
        return ResponseEntity.ok(ApiResponse.<List<ProjectDocumentDto>>builder()
                .success(true).code("DOCUMENTS_RETRIEVED").data(documents).build());
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable UUID documentId) {
        projectDocumentService.deleteDocument(documentId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).code("DOCUMENT_DELETED").message("Document deleted").build());
    }
}