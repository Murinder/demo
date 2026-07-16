-- Lessons table for teacher schedule
CREATE TABLE lessons (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    time_slot VARCHAR(20) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    lesson_type VARCHAR(20) NOT NULL,
    group_name VARCHAR(100),
    room VARCHAR(100),
    semester INTEGER,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_lessons_user_id ON lessons(user_id);
CREATE INDEX idx_lessons_user_semester ON lessons(user_id, semester);

-- Defenses table for defense schedule
CREATE TABLE defenses (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    student_name VARCHAR(255),
    supervisor_id UUID NOT NULL,
    defense_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED',
    project_title VARCHAR(500),
    defense_date DATE,
    defense_time VARCHAR(20),
    room VARCHAR(100),
    grade INTEGER,
    reviewers_count INTEGER,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_defenses_supervisor_id ON defenses(supervisor_id);
CREATE INDEX idx_defenses_student_id ON defenses(student_id);
