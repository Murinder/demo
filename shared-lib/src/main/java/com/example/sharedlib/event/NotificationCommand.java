package com.example.sharedlib.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Command to send a notification to a user. Published by any service,
 * consumed by core-service notification subsystem.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotificationCommand extends BaseEvent {
    private UUID userId;
    private String title;
    private String message;
    private String type;
    private String channel;
}