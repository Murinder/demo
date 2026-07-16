-- =============================================================
-- V007: Add unique constraints to prevent duplicate entities
-- =============================================================

-- 1. Partial unique index: only one PENDING application per (student, lecturer, kind)
DELETE FROM applications a
    USING applications b
    WHERE a.student_id = b.student_id
      AND a.lecturer_id = b.lecturer_id
      AND a.kind = b.kind
      AND a.status = b.status
      AND a.status = 'PENDING'
      AND a.submitted_at > b.submitted_at;

CREATE UNIQUE INDEX IF NOT EXISTS uq_pending_application
    ON applications (student_id, lecturer_id, kind)
    WHERE status = 'PENDING';

-- 2. Faculty name must be unique
DELETE FROM faculties a
    USING faculties b
    WHERE a.name = b.name
      AND a.created_at > b.created_at;

ALTER TABLE faculties
    ADD CONSTRAINT uq_faculty_name UNIQUE (name);

-- 3. Department name must be unique within a faculty
DELETE FROM departments a
    USING departments b
    WHERE a.faculty_id = b.faculty_id
      AND a.name = b.name
      AND a.created_at > b.created_at;

ALTER TABLE departments
    ADD CONSTRAINT uq_department_name_faculty UNIQUE (faculty_id, name);

-- 4. Emergency contact phone must be unique per user
DELETE FROM emergency_contacts a
    USING emergency_contacts b
    WHERE a.user_id = b.user_id
      AND a.contact_phone = b.contact_phone
      AND a.id > b.id;

ALTER TABLE emergency_contacts
    ADD CONSTRAINT uq_emergency_contact_phone UNIQUE (user_id, contact_phone);

-- 5. Session token must be unique
DELETE FROM sessions a
    USING sessions b
    WHERE a.token = b.token
      AND a.created_at > b.created_at;

ALTER TABLE sessions
    ADD CONSTRAINT uq_session_token UNIQUE (token);
