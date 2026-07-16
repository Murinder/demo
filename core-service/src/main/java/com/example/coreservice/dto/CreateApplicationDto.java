package com.example.coreservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateApplicationDto {
    private String title;          // optional for new kinds (auto-generated)
    private String description;
    @NotNull
    private UUID lecturerId;
    @NotNull
    private String kind;       // PROJECT, CONSULT, VKR, RESOURCE_REQUEST, EQUIPMENT_REQUEST, ROOM_REQUEST, OTHER
    private String priority;   // HIGH, MEDIUM, LOW
    private String category;
    private String duration;
    private String teamSize;
}
