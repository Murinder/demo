package com.example.documentservice.repository;

import com.example.documentservice.model.entity.DocumentTemplate;
import com.example.documentservice.model.enums.DocumentType;
import com.example.documentservice.model.enums.TemplateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, UUID> {

    List<DocumentTemplate> findByDocumentType(DocumentType documentType);

    List<DocumentTemplate> findByStatus(TemplateStatus status);

    List<DocumentTemplate> findByIsPublicTrue();

    List<DocumentTemplate> findByFacultyId(UUID facultyId);

    List<DocumentTemplate> findByDepartmentId(UUID departmentId);
}
