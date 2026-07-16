package com.example.documentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentSignatureDto {
    private UUID documentId;
    private UUID signerId;
    private OffsetDateTime signedAt;
    private String signatureType;
    private String ipAddress;
    private String userAgent;
    private String comment;
}
