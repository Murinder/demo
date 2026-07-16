package com.example.partnerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgreementDto {
    private UUID id;
    private UUID partnerId;
    private UUID projectId;
    private String documentPath;
    private String status;
    private OffsetDateTime signedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime expiresAt;
    private Integer version;
    private String description;
}
