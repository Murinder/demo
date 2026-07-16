-- =============================================================
-- V003: Add unique constraints to prevent duplicate entities
-- =============================================================

-- 1. Project template name must be unique
DELETE FROM project_templates a
    USING project_templates b
    WHERE a.name = b.name
      AND a.created_at > b.created_at;

ALTER TABLE project_templates
    ADD CONSTRAINT uq_project_template_name UNIQUE (name);

-- 2. Project document must be unique by (project, file_path, version)
DELETE FROM project_documents a
    USING project_documents b
    WHERE a.project_id = b.project_id
      AND a.file_path = b.file_path
      AND a.version = b.version
      AND a.created_at > b.created_at;

ALTER TABLE project_documents
    ADD CONSTRAINT uq_project_doc_file_version UNIQUE (project_id, file_path, version);
