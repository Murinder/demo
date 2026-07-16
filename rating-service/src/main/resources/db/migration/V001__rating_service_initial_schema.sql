-- ENUM types
CREATE TYPE verification_status AS ENUM ('PENDING', 'VERIFIED', 'REJECTED');
CREATE TYPE rating_criteria_type AS ENUM ('STUDENT', 'LECTURER', 'DEPARTMENT');

-- Rating criteria
CREATE TABLE rating_criteria (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    weight NUMERIC(5,2) NOT NULL DEFAULT 1.0,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    criteria_type rating_criteria_type NOT NULL,
    base_points INTEGER NOT NULL DEFAULT 0,
    max_points INTEGER
);

-- Student ratings
CREATE TABLE student_ratings (
    user_id UUID PRIMARY KEY,
    total_score NUMERIC(10,2) NOT NULL DEFAULT 0,
    calculation_details JSONB NOT NULL DEFAULT '{}'::jsonb,
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    semester INTEGER NOT NULL,
    verification_status verification_status DEFAULT 'PENDING',
    verified_by UUID,
    verified_at TIMESTAMPTZ
);

-- Lecturer ratings
CREATE TABLE lecturer_ratings (
    user_id UUID PRIMARY KEY,
    total_score NUMERIC(10,2) NOT NULL DEFAULT 0,
    calculation_details JSONB NOT NULL DEFAULT '{}'::jsonb,
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    semester INTEGER NOT NULL,
    verification_status verification_status DEFAULT 'PENDING',
    verified_by UUID,
    verified_at TIMESTAMPTZ
);

-- Department ratings
CREATE TABLE department_ratings (
    department_id UUID PRIMARY KEY,
    total_score NUMERIC(10,2) NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    semester INTEGER NOT NULL,
    calculation_details JSONB NOT NULL DEFAULT '{}'::jsonb,
    faculty_id UUID NOT NULL
);

-- Rating history
CREATE TABLE rating_history (
    id UUID PRIMARY KEY,
    user_id UUID,
    department_id UUID,
    score NUMERIC(10,2) NOT NULL,
    calculated_at TIMESTAMPTZ DEFAULT NOW(),
    reason VARCHAR(100) NOT NULL,
    criteria_id UUID REFERENCES rating_criteria(id),
    related_entity_id UUID,
    semester INTEGER NOT NULL
);

-- Rating settings
CREATE TABLE rating_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    semester INTEGER NOT NULL,
    calculation_algorithm VARCHAR(100) NOT NULL,
    last_calculation TIMESTAMPTZ,
    is_active BOOLEAN DEFAULT true,
    parameters JSONB NOT NULL DEFAULT '{}'::jsonb
);

-- Indexes
CREATE INDEX idx_rating_criteria_type ON rating_criteria(criteria_type);
CREATE INDEX idx_rating_history_user_id ON rating_history(user_id);
CREATE INDEX idx_rating_history_department_id ON rating_history(department_id);
CREATE INDEX idx_rating_history_semester ON rating_history(semester);
