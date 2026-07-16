package com.example.sharedlib.event;

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
public class ProjectEvent extends BaseEvent {
    private UUID projectId;
    private String title;
    private UUID createdBy;
    private UUID departmentId;
    private String oldStatus;
    private String newStatus;
    private UUID userId;
    private String role;
    private UUID taskId;
    private UUID documentId;
}