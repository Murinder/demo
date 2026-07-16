package com.example.sharedlib.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Base class for all domain events published via RabbitMQ.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent implements Serializable {
    private String eventId;
    private OffsetDateTime timestamp;
    private String source;

    /**
     * Generates a new eventId and sets timestamp/source.
     */
    public void init(String source) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = OffsetDateTime.now();
        this.source = source;
    }
}