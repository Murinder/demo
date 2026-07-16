package com.example.documentservice.controller;

import com.example.documentservice.dto.CreateTemplateDto;
import com.example.documentservice.dto.DocumentTemplateDto;
import com.example.documentservice.dto.PlaceholderDto;
import com.example.documentservice.service.DocumentTemplateService;
import com.example.documentservice.service.PlaceholderService;
import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.security.AdminOnly;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/document-templates")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Document Templates", description = "Управление шаблонами документов")
public class DocumentTemplateController {

    private final DocumentTemplateService templateService;
    private final PlaceholderService placeholderService;

    @Operation(summary = "Create a new document template")
    @PostMapping
    @AdminOnly
    public ResponseEntity<ApiResponse<DocumentTemplateDto>> createTemplate(
            @Valid @RequestBody CreateTemplateDto dto,
            Authentication authentication) {
        UUID createdBy = UUID.fromString(authentication.getName());
        DocumentTemplateDto result = templateService.createTemplate(dto, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result, "Template created successfully"));
    }

    @Operation(summary = "Get all document templates")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentTemplateDto>>> getAllTemplates() {
        List<DocumentTemplateDto> templates = templateService.getAllTemplates();
        return ResponseEntity.ok(ApiResponse.success(templates));
    }

    @Operation(summary = "Get a document template by ID")
    @GetMapping("/{templateId}")
    public ResponseEntity<ApiResponse<DocumentTemplateDto>> getTemplate(@PathVariable UUID templateId) {
        DocumentTemplateDto template = templateService.getTemplateById(templateId);
        return ResponseEntity.ok(ApiResponse.success(template));
    }

    @Operation(summary = "Update a document template")
    @PutMapping("/{templateId}")
    @AdminOnly
    public ResponseEntity<ApiResponse<DocumentTemplateDto>> updateTemplate(
            @PathVariable UUID templateId,
            @Valid @RequestBody CreateTemplateDto dto) {
        DocumentTemplateDto result = templateService.updateTemplate(templateId, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Template updated successfully"));
    }

    @Operation(summary = "Delete a document template")
    @DeleteMapping("/{templateId}")
    @AdminOnly
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(@PathVariable UUID templateId) {
        templateService.deleteTemplate(templateId);
        return ResponseEntity.ok(ApiResponse.success(null, "Template deleted successfully"));
    }

    @Operation(summary = "Get placeholders for a document template")
    @GetMapping("/{templateId}/placeholders")
    public ResponseEntity<ApiResponse<List<PlaceholderDto>>> getPlaceholders(@PathVariable UUID templateId) {
        List<PlaceholderDto> placeholders = placeholderService.getPlaceholdersByTemplateId(templateId);
        return ResponseEntity.ok(ApiResponse.success(placeholders));
    }
}
