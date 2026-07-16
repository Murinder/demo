package com.example.eventservice.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLessonDto {
    private UUID userId;
    private String dayOfWeek;
    private String timeSlot;
    private String subject;
    private String lessonType;
    private String groupName;
    private String room;
    private Integer semester;
    private String lessonDate;
}
