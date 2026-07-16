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
public class CreateAgreementDto {
    private UUID partnerId;
    private UUID projectId;
    private String documentPath;
    private OffsetDateTime expiresAt;
    private String description;
}
