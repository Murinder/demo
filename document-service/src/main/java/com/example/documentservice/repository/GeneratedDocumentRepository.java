package com.example.documentservice.repository;

import com.example.documentservice.model.entity.GeneratedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GeneratedDocumentRepository extends JpaRepository<GeneratedDocument, UUID> {

    List<GeneratedDocument> findByTemplateId(UUID templateId);

    List<GeneratedDocument> findByGeneratedFor(UUID generatedFor);

    List<GeneratedDocument> findByGeneratedBy(UUID generatedBy);
}
