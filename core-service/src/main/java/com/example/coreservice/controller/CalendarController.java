package com.example.coreservice.controller;

import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/calendar")
@Tag(name = "Calendar", description = "Calendar export and subscription endpoints")
public class CalendarController {

    @AuthenticatedOnly
    @GetMapping(value = "/export.ics", produces = "text/calendar")
    @Operation(summary = "Export calendar", description = "Returns a minimal iCalendar file with events")
    public ResponseEntity<String> exportCalendar() {
        log.info("Exporting calendar as iCalendar format");

        String ical = "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:-//ETSOPY//University Platform//EN\r\n" +
                "BEGIN:VEVENT\r\n" +
                "DTSTART:20260401T090000Z\r\n" +
                "DTEND:20260401T103000Z\r\n" +
                "SUMMARY:Software Engineering Lecture\r\n" +
                "DESCRIPTION:Weekly lecture - Room 301\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/calendar"))
                .body(ical);
    }

    @AuthenticatedOnly
    @GetMapping("/subscribe")
    @Operation(summary = "Get subscription URL", description = "Returns a unique calendar subscription URL")
    public ResponseEntity<String> subscribe() {
        String subscriptionId = UUID.randomUUID().toString();
        String subscriptionUrl = "/api/v1/calendar/feed/" + subscriptionId + ".ics";
        log.info("Generated calendar subscription URL: {}", subscriptionUrl);
        return ResponseEntity.ok(subscriptionUrl);
    }
}
