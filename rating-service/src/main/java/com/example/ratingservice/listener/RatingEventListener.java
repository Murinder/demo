package com.example.ratingservice.listener;

import com.example.ratingservice.service.RatingCalculationService;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RatingEventListener {

    private final RatingCalculationService calculationService;

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_USER_CREATED_RATING)
    public void onUserCreated(Map<String, Object> event) {
        log.info("New user registered, initializing rating: {}", event);
        String userIdStr = String.valueOf(event.get("userId"));
        calculationService.initializeStudentRating(UUID.fromString(userIdStr));
    }

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_PROJECT_STATUS_RATING)
    public void onProjectStatusChanged(Map<String, Object> event) {
        log.info("Project status changed: {}", event);
        String newStatus = (String) event.get("newStatus");
        if ("COMPLETED".equals(newStatus)) {
            UUID projectId = UUID.fromString(String.valueOf(event.get("projectId")));
            calculationService.onProjectCompleted(projectId);
        }
    }

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_PROJECT_TASK_COMPLETED_RATING)
    public void onTaskCompleted(Map<String, Object> event) {
        log.info("Task completed: {}", event);
        Object userIdObj = event.get("userId");
        if (userIdObj != null) {
            UUID userId = UUID.fromString(String.valueOf(userIdObj));
            UUID taskId = UUID.fromString(String.valueOf(event.get("taskId")));
            UUID projectId = UUID.fromString(String.valueOf(event.get("projectId")));
            calculationService.onTaskCompleted(userId, taskId, projectId);
        }
    }

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_EVENT_COMPLETED_RATING)
    public void onEventCompleted(Map<String, Object> event) {
        log.info("Event completed: {}", event);
        // Award points to event participants
    }

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_EVENT_DECIDED_RATING)
    public void onApplicationDecided(Map<String, Object> event) {
        log.info("Application decided: {}", event);
        String status = (String) event.get("applicationStatus");
        if ("APPROVED".equals(status)) {
            Object userIdObj = event.get("userId");
            if (userIdObj != null) {
                UUID userId = UUID.fromString(String.valueOf(userIdObj));
                Object eventIdObj = event.get("eventId") != null ? event.get("eventId") : event.get("domainEventId");
                UUID eventId = eventIdObj != null ? UUID.fromString(String.valueOf(eventIdObj)) : null;
                calculationService.onEventParticipation(userId, eventId);
            }
        }
    }

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_PORTFOLIO_ACHIEVEMENT_RATING)
    public void onAchievementAdded(Map<String, Object> event) {
        log.info("Achievement added: {}", event);
        Object userIdObj = event.get("userId");
        if (userIdObj != null) {
            UUID userId = UUID.fromString(String.valueOf(userIdObj));
            Object achievementIdObj = event.get("achievementId");
            UUID achievementId = achievementIdObj != null ? UUID.fromString(String.valueOf(achievementIdObj)) : null;
            calculationService.onAchievementAdded(userId, achievementId);
        }
    }
}
