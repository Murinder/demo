-- =============================================================
-- V002: Add unique constraints to prevent duplicate entities
-- =============================================================

-- 1. Document template must be unique by (name, type, version)
DELETE FROM document_templates a
    USING document_templates b
    WHERE a.name = b.name
      AND a.document_type = b.document_type
      AND a.version = b.version
      AND a.created_at > b.created_at;

ALTER TABLE document_templates
    ADD CONSTRAINT uq_template_name_type_version UNIQUE (name, document_type, version);
