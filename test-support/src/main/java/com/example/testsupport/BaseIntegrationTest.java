package com.example.testsupport;

import com.example.testsupport.containers.PostgresTestContainer;
import com.example.testsupport.containers.RabbitMqTestContainer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Base class for integration tests that require PostgreSQL and RabbitMQ.
 * Each service module runs in its own surefire JVM fork, so the singleton
 * container is created once per module. Flyway clean-on-validation-error
 * ensures each service can run its own migrations cleanly.
 */
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL
        registry.add("spring.datasource.url", PostgresTestContainer::getJdbcUrl);
        registry.add("spring.datasource.username", PostgresTestContainer::getUsername);
        registry.add("spring.datasource.password", PostgresTestContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        // Flyway — clean on validation error so each service can run its own migrations
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.clean-disabled", () -> "false");
        registry.add("spring.flyway.clean-on-validation-error", () -> "true");

        // JPA
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.jpa.show-sql", () -> "true");

        // RabbitMQ
        registry.add("spring.rabbitmq.host", RabbitMqTestContainer::getHost);
        registry.add("spring.rabbitmq.port", RabbitMqTestContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", RabbitMqTestContainer::getUsername);
        registry.add("spring.rabbitmq.password", RabbitMqTestContainer::getPassword);

        // Disable Eureka
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");

        // JWT test secret
        registry.add("app.jwt.secret",
                () -> "test-secret-key-for-testing-only-minimum-32-characters-long");
        registry.add("app.jwt.expiration", () -> "3600000");
    }
}
