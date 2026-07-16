package com.example.sharedlib.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RatingEvent extends BaseEvent {
    private UUID userId;
    private UUID departmentId;
    private BigDecimal newScore;
    private Map<String, Object> details;
}