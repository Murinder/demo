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
public class PartnerContactDto {
    private UUID id;
    private UUID partnerId;
    private String name;
    private String position;
    private String email;
    private String phone;
    private Boolean isPrimary;
    private OffsetDateTime createdAt;
}
