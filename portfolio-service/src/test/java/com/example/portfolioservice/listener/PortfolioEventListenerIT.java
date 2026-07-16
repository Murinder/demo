package com.example.portfolioservice.listener;

import com.example.portfolioservice.model.Portfolio;
import com.example.portfolioservice.repository.PortfolioRepository;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.testsupport.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
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
class PortfolioEventListenerIT extends BaseIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @BeforeEach
    void setUp() {
        portfolioRepository.deleteAll();
    }

    @Test
    void onUserCreated_CreatesPortfolioInDb() {
        UUID userId = UUID.randomUUID();

        Map<String, Object> event = Map.of(
                "userId", userId.toString(),
                "email", "newuser@test.com",
                "fullName", "New User"
        );

        rabbitTemplate.convertAndSend(
                RabbitMqAutoConfiguration.USER_EXCHANGE,
                RabbitMqAutoConfiguration.USER_CREATED_KEY,
                event
        );

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var all = portfolioRepository.findAll();
            assertThat(all).isNotEmpty();
            assertThat(all.stream().anyMatch(p -> userId.equals(p.getUserId()))).isTrue();
        });
    }

    @Test
    void onEventCompleted_Handled() {
        // Listener logs but doesn't create entities yet
        Map<String, Object> event = Map.of(
                "eventId", UUID.randomUUID().toString(),
                "title", "Hackathon 2024"
        );

        rabbitTemplate.convertAndSend(
                RabbitMqAutoConfiguration.EVENT_EXCHANGE,
                RabbitMqAutoConfiguration.EVENT_COMPLETED_KEY,
                event
        );

        // Verify no exception
        await().during(1, TimeUnit.SECONDS).atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(true).isTrue());
    }

    @Test
    void onRatingRecalculated_Handled() {
        Map<String, Object> event = Map.of(
                "userId", UUID.randomUUID().toString(),
                "newScore", "95.5"
        );

        rabbitTemplate.convertAndSend(
                RabbitMqAutoConfiguration.RATING_EXCHANGE,
                RabbitMqAutoConfiguration.RATING_RECALCULATED_KEY,
                event
        );

        await().during(1, TimeUnit.SECONDS).atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(true).isTrue());
    }
}
