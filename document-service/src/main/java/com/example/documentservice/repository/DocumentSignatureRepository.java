package com.example.documentservice.repository;

import com.example.documentservice.model.entity.DocumentSignature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentSignatureRepository extends JpaRepository<DocumentSignature, DocumentSignature.DocumentSignatureId> {

    List<DocumentSignature> findByIdDocumentId(UUID documentId);

    List<DocumentSignature> findByIdSignerId(UUID signerId);
}
