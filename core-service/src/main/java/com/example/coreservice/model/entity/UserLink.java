package com.example.coreservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import com.example.coreservice.model.enums.LinkType;
import java.util.UUID;



@Entity
@Table(name = "user_links")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLink {
    @EmbeddedId
    private UserLinkId id;

    @Column(name = "url", nullable = false, length = 512)
    private String url;
}