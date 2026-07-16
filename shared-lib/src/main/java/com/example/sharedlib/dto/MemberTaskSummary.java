package com.example.sharedlib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberTaskSummary {
    private UUID userId;
    private String firstName;
    private String lastName;
    private int tasksDone;
    private int tasksTotal;
    private OffsetDateTime lastActive;
}
