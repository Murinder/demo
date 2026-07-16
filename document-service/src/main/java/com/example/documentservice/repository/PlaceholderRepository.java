package com.example.documentservice.repository;

import com.example.documentservice.model.entity.Placeholder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlaceholderRepository extends JpaRepository<Placeholder, Placeholder.PlaceholderId> {

    List<Placeholder> findByIdTemplateId(UUID templateId);

    void deleteByIdTemplateId(UUID templateId);
}
