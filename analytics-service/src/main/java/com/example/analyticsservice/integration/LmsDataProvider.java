package com.example.analyticsservice.integration;

import java.util.Map;
import java.util.UUID;

/**
 * Interface for LMS (Learning Management System) data integration.
 */
public interface LmsDataProvider {
    Map<String, Object> getGrades(UUID userId, Integer semester);
    void syncUsers();
}
