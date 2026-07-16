package com.example.projectservice.integration;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Interface for Megalaboratories resource booking integration.
 */
public interface ResourceBookingService {
    Map<String, Object> getAvailability(UUID resourceId, LocalDate date);
    Map<String, Object> bookResource(UUID resourceId, UUID projectId, String timeSlot);
}
