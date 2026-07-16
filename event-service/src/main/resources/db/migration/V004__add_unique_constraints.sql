-- =============================================================
-- V004: Add unique constraints to prevent duplicate entities
-- =============================================================

-- 1. A user can only apply once per event
DELETE FROM event_applications a
    USING event_applications b
    WHERE a.event_id = b.event_id
      AND a.user_id = b.user_id
      AND a.created_at > b.created_at;

ALTER TABLE event_applications
    ADD CONSTRAINT uq_event_application_user UNIQUE (event_id, user_id);

-- 2. Team name must be unique within an event
DELETE FROM teams a
    USING teams b
    WHERE a.event_id = b.event_id
      AND a.name = b.name
      AND a.created_at > b.created_at;

ALTER TABLE teams
    ADD CONSTRAINT uq_team_name_per_event UNIQUE (event_id, name);

-- 3. A teacher cannot have duplicate schedule slots
DELETE FROM lessons a
    USING lessons b
    WHERE a.user_id = b.user_id
      AND a.day_of_week = b.day_of_week
      AND a.time_slot = b.time_slot
      AND a.semester = b.semester
      AND a.created_at > b.created_at;

ALTER TABLE lessons
    ADD CONSTRAINT uq_lesson_slot UNIQUE (user_id, day_of_week, time_slot, semester);
