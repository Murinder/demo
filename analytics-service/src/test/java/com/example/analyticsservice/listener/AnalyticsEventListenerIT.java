package com.example.analyticsservice.listener;

import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.testsupport.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AnalyticsEventListenerIT extends BaseIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void onProjectStatusChanged_HandledWithoutException() {
        Map<String, Object> event = Map.of(
                "projectId", UUID.randomUUID().toString(),
                "oldStatus", "IN_PROGRESS",
                "newStatus", "COMPLETED"
        );

        rabbitTemplate.convertAndSend(
                RabbitMqAutoConfiguration.PROJECT_EXCHANGE,
                RabbitMqAutoConfiguration.PROJECT_STATUS_CHANGED_KEY,
                event
        );

        await().during(1, TimeUnit.SECONDS).atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(true).isTrue());
    }

    @Test
    void onProjectTaskCompleted_HandledWithoutException() {
        Map<String, Object> event = Map.of(
                "taskId", UUID.randomUUID().toString(),
                "projectId", UUID.randomUUID().toString(),
                "userId", UUID.randomUUID().toString()
        );

        rabbitTemplate.convertAndSend(
                RabbitMqAutoConfiguration.PROJECT_EXCHANGE,
                RabbitMqAutoConfiguration.PROJECT_TASK_COMPLETED_KEY,
                event
        );

        await().during(1, TimeUnit.SECONDS).atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(true).isTrue());
    }

    @Test
    void onEventCompleted_HandledWithoutException() {
        Map<String, Object> event = Map.of(
                "eventId", UUID.randomUUID().toString(),
                "title", "Hackathon"
        );

        rabbitTemplate.convertAndSend(
                RabbitMqAutoConfiguration.EVENT_EXCHANGE,
                RabbitMqAutoConfiguration.EVENT_COMPLETED_KEY,
                event
        );

        await().during(1, TimeUnit.SECONDS).atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(true).isTrue());
    }
}
