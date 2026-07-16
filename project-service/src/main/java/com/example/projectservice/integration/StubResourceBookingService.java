package com.example.projectservice.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Stub implementation of Megalaboratories resource booking service.
 * Returns mock availability data for development/testing.
 */
@Slf4j
@Service
public class StubResourceBookingService implements ResourceBookingService {

    @Override
    public Map<String, Object> getAvailability(UUID resourceId, LocalDate date) {
        log.info("[STUB MEGALAB] Getting availability for resource {} on {}", resourceId, date);

        return Map.of(
                "resourceId", resourceId.toString(),
                "date", date.toString(),
                "available", true,
                "slots", List.of(
                        Map.of("time", "09:00-11:00", "available", true),
                        Map.of("time", "11:00-13:00", "available", true),
                        Map.of("time", "13:00-15:00", "available", false),
                        Map.of("time", "15:00-17:00", "available", true)
                )
        );
    }

    @Override
    public Map<String, Object> bookResource(UUID resourceId, UUID projectId, String timeSlot) {
        log.info("[STUB MEGALAB] Booking resource {} for project {} at {}", resourceId, projectId, timeSlot);

        return Map.of(
                "bookingId", UUID.randomUUID().toString(),
                "resourceId", resourceId.toString(),
                "projectId", projectId.toString(),
                "timeSlot", timeSlot,
                "status", "CONFIRMED"
        );
    }
}
