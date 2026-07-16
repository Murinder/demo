package com.example.analyticsservice.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Stub implementation of LMS data provider.
 * Returns hardcoded test data for development/testing.
 */
@Slf4j
@Service
public class StubLmsDataProvider implements LmsDataProvider {

    @Override
    public Map<String, Object> getGrades(UUID userId, Integer semester) {
        log.info("[STUB LMS] Getting grades for user {} semester {}", userId, semester);

        Map<String, Object> grades = new HashMap<>();
        grades.put("userId", userId.toString());
        grades.put("semester", semester);
        grades.put("gpa", 4.0);
        grades.put("totalCredits", 30);
        grades.put("courses", List.of(
                Map.of("name", "Software Engineering", "grade", "A", "credits", 6),
                Map.of("name", "Database Systems", "grade", "A", "credits", 6),
                Map.of("name", "Computer Networks", "grade", "A-", "credits", 6),
                Map.of("name", "Operating Systems", "grade", "A", "credits", 6),
                Map.of("name", "Algorithms", "grade", "A", "credits", 6)
        ));
        return grades;
    }

    @Override
    public void syncUsers() {
        log.info("[STUB LMS] Syncing users from LMS - no-op in stub mode");
    }
}
