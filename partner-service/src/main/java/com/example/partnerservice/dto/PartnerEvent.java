package com.example.partnerservice.dto;

import com.example.sharedlib.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PartnerEvent extends BaseEvent {
    private UUID entityId;
    private UUID partnerId;
    private String title;
    private String entityType;
}
