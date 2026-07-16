-- =============================================================
-- V003: Add unique constraints to prevent duplicate entities
-- =============================================================

-- 1. Rating criteria name must be unique
DELETE FROM rating_criteria a
    USING rating_criteria b
    WHERE a.name = b.name
      AND a.created_at > b.created_at;

ALTER TABLE rating_criteria
    ADD CONSTRAINT uq_rating_criteria_name UNIQUE (name);

-- 2. Only one rating settings entry per semester
DELETE FROM rating_settings a
    USING rating_settings b
    WHERE a.semester = b.semester
      AND a.id > b.id;

ALTER TABLE rating_settings
    ADD CONSTRAINT uq_rating_settings_semester UNIQUE (semester);

-- Clarify intent: these tables store the CURRENT/LATEST rating only.
-- Historical per-semester data is tracked in rating_history.
COMMENT ON TABLE student_ratings IS 'Stores the current (latest) student rating. Historical data is in rating_history.';
COMMENT ON TABLE lecturer_ratings IS 'Stores the current (latest) lecturer rating. Historical data is in rating_history.';
COMMENT ON TABLE department_ratings IS 'Stores the current (latest) department rating. Historical data is in rating_history.';
