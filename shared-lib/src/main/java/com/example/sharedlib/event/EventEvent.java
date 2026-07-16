package com.example.sharedlib.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Events related to the Event (мероприятие) domain.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EventEvent extends BaseEvent {
    private UUID domainEventId;
    private String title;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private String oldStatus;
    private String newStatus;
    private UUID applicationId;
    private UUID userId;
    private String applicationStatus;
    private UUID teamId;
    private List<UUID> memberIds;
    private Map<String, Object> results;
}