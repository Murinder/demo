package com.example.eventservice.controller;

import com.example.eventservice.dto.EventViewDto;
import com.example.eventservice.dto.TeacherEventViewDto;
import com.example.eventservice.model.Event;
import com.example.eventservice.repository.EventApplicationRepository;
import com.example.eventservice.repository.EventRepository;
import com.example.sharedlib.enums.EventFormat;
import com.example.sharedlib.enums.EventStatus;
import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.sharedlib.security.UserContext;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Frontend-facing endpoints that return data in the shape expected by the React UI.
 */
@RestController
@RequestMapping("/api/v1/events/view")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Event Views", description = "Frontend-facing event endpoints")
public class EventViewController {

    private final EventRepository eventRepository;
    private final EventApplicationRepository applicationRepository;

    private static final ZoneId MOSCOW_ZONE = ZoneId.of("Europe/Moscow");

    private static final String[] MONTHS_GENITIVE = {
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"
    };

    @Operation(summary = "Get events for student view")
    @GetMapping("/student")
    public ResponseEntity<ApiResponse<List<EventViewDto>>> getStudentEvents(
            @RequestParam(required = false) UUID userId) {

        OffsetDateTime now = OffsetDateTime.now();
        List<Event> events = eventRepository.findAll().stream()
                .filter(e -> e.getStatus() != EventStatus.DRAFT)
                .collect(Collectors.toList());
        Set<UUID> registeredEventIds = new HashSet<>();

        if (userId != null) {
            applicationRepository.findByUserId(userId)
                    .forEach(a -> registeredEventIds.add(a.getEvent().getId()));
        }

        List<EventViewDto> result = events.stream()
                .map(e -> mapToStudentView(e, registeredEventIds, now))
                .sorted(Comparator.comparing(EventViewDto::isPast)
                        .thenComparing(dto -> dto.getDate(), Comparator.reverseOrder()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<EventViewDto>>builder()
                .success(true)
                .code("EVENTS_RETRIEVED")
                .message("Student events retrieved successfully")
                .data(result)
                .build());
    }

    @Operation(summary = "Get events for teacher view")
    @GetMapping("/teacher")
    public ResponseEntity<ApiResponse<List<TeacherEventViewDto>>> getTeacherEvents(
            @RequestParam UUID userId) {

        UUID callerId = UserContext.getCurrentUserId();
        if (callerId != null && !callerId.equals(userId)
                && !UserContext.hasRole("ADMIN") && !UserContext.hasRole("DEPARTMENT_HEAD")) {
            return ResponseEntity.status(403).body(ApiResponse.<List<TeacherEventViewDto>>builder()
                    .success(false)
                    .code("ACCESS_DENIED")
                    .message("You can only view your own events")
                    .data(Collections.emptyList())
                    .build());
        }

        List<Event> events = eventRepository.findByCreatedBy(userId);

        List<TeacherEventViewDto> result = events.stream()
                .map(this::mapToTeacherView)
                .sorted(Comparator.comparing(TeacherEventViewDto::getDateISO))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<TeacherEventViewDto>>builder()
                .success(true)
                .code("TEACHER_EVENTS_RETRIEVED")
                .message("Teacher events retrieved successfully")
                .data(result)
                .build());
    }

    private EventViewDto mapToStudentView(Event event, Set<UUID> registeredEventIds, OffsetDateTime now) {
        int applicationsCount = event.getApplications() != null ? event.getApplications().size() : 0;
        int maxParticipants = event.getMaxParticipants() != null ? event.getMaxParticipants() : 0;
        boolean isPast = event.getEndDate() != null && event.getEndDate().isBefore(now);

        ZonedDateTime startLocal = event.getStartDate().atZoneSameInstant(MOSCOW_ZONE);
        ZonedDateTime endLocal = event.getEndDate() != null
                ? event.getEndDate().atZoneSameInstant(MOSCOW_ZONE) : null;

        String dateFormatted = formatDateRussian(startLocal);
        String time = startLocal.format(DateTimeFormatter.ofPattern("HH:mm"));

        return EventViewDto.builder()
                .id(event.getId().toString())
                .tag(mapTag(event))
                .title(event.getTitle())
                .description(event.getDescription() != null ? event.getDescription() : "")
                .date(dateFormatted)
                .time(time)
                .place(event.getLocation() != null ? event.getLocation() : "Не указано")
                .participants(maxParticipants > 0
                        ? applicationsCount + "/" + maxParticipants + " участников"
                        : applicationsCount + " участников")
                .chips(inferChips(event))
                .isRegistered(registeredEventIds.contains(event.getId()))
                .isPast(isPast)
                .dateISO(startLocal.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .endDateISO(endLocal != null
                        ? endLocal.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        : null)
                .organizerName(event.getOrganizerName())
                .eventType(event.getEventType())
                .build();
    }

    private TeacherEventViewDto mapToTeacherView(Event event) {
        long durationMin = 60;
        if (event.getStartDate() != null && event.getEndDate() != null) {
            durationMin = Duration.between(event.getStartDate(), event.getEndDate()).toMinutes();
        }

        int participantCount = event.getApplications() != null ? event.getApplications().size() : 0;

        String type = event.getEventType() != null ? event.getEventType() : "Другое";

        ZonedDateTime startLocal = event.getStartDate().atZoneSameInstant(MOSCOW_ZONE);

        return TeacherEventViewDto.builder()
                .id(event.getId().toString())
                .type(type)
                .title(event.getTitle())
                .subtitle(event.getDescription() != null ? event.getDescription() : "")
                .dateISO(startLocal.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .time(startLocal.format(DateTimeFormatter.ofPattern("HH:mm")))
                .durationMin((int) durationMin)
                .place(event.getLocation() != null ? event.getLocation() : "Не указано")
                .participantCount(participantCount)
                .status(event.getStatus().name())
                .build();
    }

    private String formatDateRussian(ZonedDateTime dt) {
        int day = dt.getDayOfMonth();
        String month = MONTHS_GENITIVE[dt.getMonthValue() - 1];
        int year = dt.getYear();
        return day + " " + month + " " + year + " г.";
    }

    private String mapTag(Event event) {
        String type = event.getEventType();
        if (type == null) return "Обучение";
        return switch (type) {
            case "Хакатон" -> "Хакатон";
            case "Конференция" -> "Конференция";
            case "Акселератор" -> "Акселератор";
            case "Карьера" -> "Карьера";
            default -> "Обучение";
        };
    }


    private List<String> inferChips(Event event) {
        List<String> chips = new ArrayList<>();
        String text = (event.getTitle() + " " + (event.getDescription() != null ? event.getDescription() : "")).toLowerCase();
        if (text.contains("ai") || text.contains("искусственн")) chips.add("AI");
        if (text.contains("ml") || text.contains("машинн")) chips.add("ML");
        if (text.contains("react")) chips.add("React");
        if (text.contains("frontend") || text.contains("фронтенд")) chips.add("Frontend");
        if (text.contains("backend") || text.contains("бэкенд")) chips.add("Backend");
        if (text.contains("карьер")) chips.add("Карьера");
        if (text.contains("стажир")) chips.add("Стажировки");
        if (chips.isEmpty()) chips.add("Обучение");
        return chips;
    }
}
