package com.example.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Frontend-facing DTO for teacher event display.
 * Matches the shape expected by the React frontend (TeacherEvent type).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherEventViewDto {
    private String id;
    private String type;           // "Консультация", "Лекция", "Семинар", "Защита"
    private String title;
    private String subtitle;
    private String dateISO;        // "2025-11-15"
    private String time;           // "14:00"
    private int durationMin;
    private String place;
    private int participantCount;
    private String status;
}
