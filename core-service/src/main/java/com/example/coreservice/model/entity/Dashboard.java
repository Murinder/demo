package com.example.coreservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "dashboards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dashboard {
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "widget_config", columnDefinition = "jsonb")
    private String widgetConfig;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}