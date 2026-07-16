-- Student applications/requests to lecturers

CREATE TYPE application_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');
CREATE TYPE application_kind AS ENUM ('PROJECT', 'CONSULT', 'VKR');
CREATE TYPE application_priority AS ENUM ('HIGH', 'MEDIUM', 'LOW');

CREATE TABLE applications (
    id UUID PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    lecturer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status application_status NOT NULL DEFAULT 'PENDING',
    priority application_priority NOT NULL DEFAULT 'MEDIUM',
    kind application_kind NOT NULL,
    category VARCHAR(255),
    duration VARCHAR(100),
    team_size VARCHAR(100),
    teacher_reply TEXT,
    submitted_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_applications_student_id ON applications(student_id);
CREATE INDEX idx_applications_lecturer_id ON applications(lecturer_id);
CREATE INDEX idx_applications_status ON applications(status);
