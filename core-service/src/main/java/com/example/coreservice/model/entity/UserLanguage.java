package com.example.coreservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import com.example.coreservice.model.enums.LanguageProficiency;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;



@Entity
@Table(name = "user_languages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLanguage {
    @EmbeddedId
    private UserLanguageId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "proficiency", nullable = false)
    private LanguageProficiency proficiency;
}