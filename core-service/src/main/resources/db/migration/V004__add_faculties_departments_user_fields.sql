-- =============================================
-- V004: Create faculties & departments tables,
--       add first_name / last_name to users
-- =============================================

-- 1. Faculties
CREATE TABLE IF NOT EXISTS faculties (
    id          UUID            PRIMARY KEY,
    name        VARCHAR(255)    NOT NULL,
    code        VARCHAR(50)     UNIQUE,
    description TEXT,
    created_at  TIMESTAMPTZ     DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     DEFAULT NOW()
);

-- 2. Departments
CREATE TABLE IF NOT EXISTS departments (
    id           UUID            PRIMARY KEY,
    name         VARCHAR(255)    NOT NULL,
    code         VARCHAR(50)     UNIQUE,
    faculty_id   UUID            NOT NULL REFERENCES faculties(id),
    head_user_id UUID,
    description  TEXT,
    created_at   TIMESTAMPTZ     DEFAULT NOW(),
    updated_at   TIMESTAMPTZ     DEFAULT NOW()
);

-- 3. Add first_name and last_name to users
ALTER TABLE users ADD COLUMN IF NOT EXISTS first_name VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_name  VARCHAR(100);

-- 4. Indexes
CREATE INDEX IF NOT EXISTS idx_departments_faculty_id
    ON departments(faculty_id);

CREATE INDEX IF NOT EXISTS idx_users_first_name_last_name
    ON users(first_name, last_name);