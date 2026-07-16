package com.example.documentservice.dto;

import com.example.documentservice.model.enums.PlaceholderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceholderDto {
    private UUID templateId;
    private String placeholder;
    private String description;
    private String dataType;
    private PlaceholderType placeholderType;
    private String exampleValue;
    private Boolean isRequired;
}
