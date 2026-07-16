package com.example.eventservice.dto;

import com.example.eventservice.model.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventApplicationDto {
    private ApplicationStatus status;
}