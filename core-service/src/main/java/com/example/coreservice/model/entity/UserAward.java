package com.example.coreservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_awards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAward {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "year", nullable = false, length = 10)
    private String year;
}
