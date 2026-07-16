package com.example.eventservice.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDefenseDto {
    private UUID studentId;
    private String studentName;
    private UUID supervisorId;
    private String defenseType;
    private String projectTitle;
    private LocalDate defenseDate;
    private String defenseTime;
    private String room;
    private Integer reviewersCount;
}
