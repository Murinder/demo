package com.example.coreservice.service;

import com.example.coreservice.dto.UserDocumentDto;
import com.example.coreservice.model.entity.UserDocument;
import com.example.coreservice.repository.UserDocumentRepository;
import com.example.sharedlib.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserDocumentService {

    private static final String BUCKET_NAME = "user-documents";

    private final UserDocumentRepository userDocumentRepository;
    private final FileStorageService fileStorageService;

    public UserDocumentDto uploadDocument(UUID userId, String description, MultipartFile file) {
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String objectName = userId + "/" + UUID.randomUUID() + "_" + originalFilename;

        try {
            fileStorageService.ensureBucketExists(BUCKET_NAME);
            fileStorageService.upload(BUCKET_NAME, objectName,
                    file.getInputStream(), file.getContentType(), file.getSize());
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file: " + originalFilename, e);
        }

        UserDocument doc = UserDocument.builder()
                .userId(userId)
                .filePath(objectName)
                .fileName(originalFilename)
                .description(description)
                .build();

        doc = userDocumentRepository.save(doc);
        log.info("Uploaded user document: {} for user {}", originalFilename, userId);
        return toDto(doc);
    }

    @Transactional(readOnly = true)
    public List<UserDocumentDto> getDocumentsByUserId(UUID userId) {
        return userDocumentRepository.findByUserIdOrderByUploadedAtDesc(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public record DocumentDownload(Resource resource, String filename) {}

    @Transactional(readOnly = true)
    public DocumentDownload downloadDocument(UUID documentId) {
        UserDocument doc = userDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));

        InputStream inputStream = fileStorageService.download(BUCKET_NAME, doc.getFilePath());
        return new DocumentDownload(new InputStreamResource(inputStream), doc.getFileName());
    }

    public void deleteDocument(UUID documentId) {
        UserDocument doc = userDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));

        try {
            fileStorageService.delete(BUCKET_NAME, doc.getFilePath());
        } catch (Exception e) {
            log.warn("Failed to delete file from storage: {}", doc.getFilePath(), e);
        }

        userDocumentRepository.delete(doc);
        log.info("Deleted user document: {} ({})", doc.getFileName(), documentId);
    }

    private UserDocumentDto toDto(UserDocument doc) {
        return UserDocumentDto.builder()
                .id(doc.getId())
                .userId(doc.getUserId())
                .filePath(doc.getFilePath())
                .fileName(doc.getFileName())
                .description(doc.getDescription())
                .uploadedAt(doc.getUploadedAt())
                .build();
    }
}
