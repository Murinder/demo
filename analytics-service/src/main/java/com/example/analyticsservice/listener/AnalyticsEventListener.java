package com.example.analyticsservice.listener;

import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsEventListener {

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_PROJECT_STATUS_ANALYTICS)
    public void onProjectStatusChanged(Map<String, Object> event) {
        log.info("Project status changed event received for analytics: {}", event);
        // Track project status transitions for KPI calculations
    }

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_PROJECT_TASK_COMPLETED_ANALYTICS)
    public void onProjectTaskCompleted(Map<String, Object> event) {
        log.info("Project task completed event received for analytics: {}", event);
        // Update task completion KPIs
    }

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_EVENT_COMPLETED_ANALYTICS)
    public void onEventCompleted(Map<String, Object> event) {
        log.info("Event completed event received for analytics: {}", event);
        // Update event success KPIs
    }
}
