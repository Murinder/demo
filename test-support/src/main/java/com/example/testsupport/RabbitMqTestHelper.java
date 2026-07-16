package com.example.testsupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

/**
 * Utility for RabbitMQ event testing.
 * Provides helpers to send events and wait for messages on queues.
 */
public class RabbitMqTestHelper {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitMqTestHelper(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Publish an event to a specific exchange with routing key.
     */
    public void publishEvent(String exchange, String routingKey, Object event) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }

    /**
     * Wait for a message to appear on a queue and return it deserialized.
     */
    public <T> T awaitMessage(String queueName, Class<T> type, Duration timeout) {
        Message[] holder = new Message[1];

        await().atMost(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    Message message = rabbitTemplate.receive(queueName, 100);
                    if (message == null) {
                        throw new AssertionError("No message on queue: " + queueName);
                    }
                    holder[0] = message;
                });

        try {
            return objectMapper.readValue(holder[0].getBody(), type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize message from queue: " + queueName, e);
        }
    }

    /**
     * Wait for a message with default 5-second timeout.
     */
    public <T> T awaitMessage(String queueName, Class<T> type) {
        return awaitMessage(queueName, type, Duration.ofSeconds(5));
    }

    /**
     * Check that a message exists on a queue (raw Message).
     */
    public Message awaitRawMessage(String queueName, Duration timeout) {
        Message[] holder = new Message[1];

        await().atMost(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    Message message = rabbitTemplate.receive(queueName, 100);
                    if (message == null) {
                        throw new AssertionError("No message on queue: " + queueName);
                    }
                    holder[0] = message;
                });

        return holder[0];
    }

    /**
     * Purge all messages from a queue.
     */
    public void purgeQueue(String queueName) {
        rabbitTemplate.execute(channel -> {
            channel.queuePurge(queueName);
            return null;
        });
    }
}
