package com.example.sharedlib.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserEvent extends BaseEvent {
    private UUID userId;
    private String email;
    private String role;
    private String fullName;
    private String oldRole;
    private String newRole;
    private Map<String, Object> changedFields;
}