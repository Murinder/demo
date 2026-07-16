package com.example.sharedlib.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Generic event publisher for sending domain events to RabbitMQ.
 * Each service uses this to publish events to the appropriate exchange with a routing key.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publishes an event to the specified exchange with the given routing key.
     *
     * @param exchange   the target exchange name (e.g. "etsopy.project")
     * @param routingKey the routing key (e.g. "project.created")
     * @param event      the event payload
     */
    public void publish(String exchange, String routingKey, BaseEvent event) {
        log.info("Publishing event to exchange={}, routingKey={}, eventId={}",
                exchange, routingKey, event.getEventId());
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
        } catch (Exception e) {
            log.error("Failed to publish event to exchange={}, routingKey={}", exchange, routingKey, e);
        }
    }
}