package com.example.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Frontend-facing DTO for event display.
 * Matches the shape expected by the React frontend (EventItem type).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventViewDto {
    private String id;
    private String tag;            // "Хакатон", "Карьера", "Обучение"
    private String title;
    private String description;
    private String date;           // formatted: "20 ноября 2025 г."
    private String time;           // "10:00"
    private String place;
    private String participants;   // "78/100 участников"
    private List<String> chips;
    private boolean isRegistered;
    private boolean isPast;
    private String dateISO;         // ISO 8601: "2026-05-15T09:00:00+03:00"
    private String endDateISO;      // ISO 8601: "2026-05-17T18:00:00+03:00"
    private String organizerName;   // resolved from created_by user
    private String eventType;       // raw event type from entity
}
