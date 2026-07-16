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
public class PartnerDto {
    private UUID id;
    private String companyName;
    private String contactInfo;
    private Boolean isActive;
    private String website;
    private String industry;
    private String partnershipStatus;
    private String logoUrl;
    private String description;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
