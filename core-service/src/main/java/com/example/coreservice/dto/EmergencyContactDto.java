package com.example.coreservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EmergencyContactDto {
    private UUID userId;
    private String contactName;
    private String contactPhone;
    private String relationship;
}