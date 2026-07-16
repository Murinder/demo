package com.example.coreservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ChatDto {
    private UUID id;
    private String name;
}