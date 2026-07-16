package com.example.projectservice.event;

import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.testsupport.BaseIntegrationTest;
import com.example.testsupport.security.TestSecurityHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ProjectEventPublishingIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        try {
            rabbitTemplate.execute(channel -> {
                channel.queuePurge(RabbitMqAutoConfiguration.QUEUE_PROJECT_CREATED_CHAT);
                channel.queuePurge(RabbitMqAutoConfiguration.QUEUE_PROJECT_CREATED_RATING);
                channel.queuePurge(RabbitMqAutoConfiguration.QUEUE_PROJECT_CREATED_SEARCH);
                return null;
            });
        } catch (Exception ignored) {}
    }

    @Test
    void createProject_PublishesProjectCreatedEvent() throws Exception {
        Map<String, Object> projectDto = Map.of(
                "title", "Test Project",
                "description", "A test project for event publishing"
        );

        mockMvc.perform(TestSecurityHelper.withLecturer(
                        post("/api/v1/projects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projectDto)),
                        UUID.randomUUID()))
                .andExpect(status().isCreated());

        // Verify message in chat queue
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var message = rabbitTemplate.receive(RabbitMqAutoConfiguration.QUEUE_PROJECT_CREATED_CHAT, 100);
            assertThat(message).isNotNull();
            String body = new String(message.getBody());
            assertThat(body).contains("Test Project");
        });
    }

    @Test
    void createProject_PublishesToRatingQueue() throws Exception {
        Map<String, Object> projectDto = Map.of(
                "title", "Rating Queue Test",
                "description", "Test"
        );

        mockMvc.perform(TestSecurityHelper.withLecturer(
                        post("/api/v1/projects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projectDto)),
                        UUID.randomUUID()))
                .andExpect(status().isCreated());

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var message = rabbitTemplate.receive(RabbitMqAutoConfiguration.QUEUE_PROJECT_CREATED_RATING, 100);
            assertThat(message).isNotNull();
        });
    }
}
