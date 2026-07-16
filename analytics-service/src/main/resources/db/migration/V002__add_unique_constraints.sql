-- =============================================================
-- V002: Add unique constraints to prevent duplicate entities
-- =============================================================

-- 1. KPI name must be unique
DELETE FROM kpi a
    USING kpi b
    WHERE a.name = b.name
      AND a.created_at > b.created_at;

ALTER TABLE kpi
    ADD CONSTRAINT uq_kpi_name UNIQUE (name);
