-- Analytics Service Schema

CREATE TYPE report_type AS ENUM ('PROJECTS', 'DEPARTMENTS', 'USERS', 'EVENTS', 'CUSTOM');
CREATE TYPE report_format AS ENUM ('XLSX', 'PDF', 'CSV');
CREATE TYPE report_status AS ENUM ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED');
CREATE TYPE kpi_calculation_method AS ENUM ('PROJECT_COMPLETION', 'STUDENT_ENGAGEMENT', 'DEPARTMENT_ACTIVITY', 'EVENT_SUCCESS', 'CUSTOM');

CREATE TABLE reports (
    id UUID PRIMARY KEY,
    report_type report_type NOT NULL,
    parameters JSONB NOT NULL,
    generated_at TIMESTAMPTZ DEFAULT NOW(),
    file_path VARCHAR(512) NOT NULL,
    scheduled BOOLEAN DEFAULT false,
    created_by UUID NOT NULL,
    format report_format NOT NULL DEFAULT 'XLSX',
    status report_status NOT NULL DEFAULT 'COMPLETED',
    error_message TEXT
);

CREATE TABLE kpi (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    calculation_method kpi_calculation_method NOT NULL,
    is_custom BOOLEAN DEFAULT false,
    created_by UUID,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    formula TEXT,
    target_value NUMERIC(10, 2)
);

CREATE TABLE kpi_values (
    kpi_id UUID NOT NULL REFERENCES kpi(id) ON DELETE CASCADE,
    value NUMERIC(10, 2) NOT NULL,
    period DATE NOT NULL,
    entity_id UUID,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (kpi_id, period, entity_id)
);

CREATE TABLE academic_performance (
    user_id UUID NOT NULL,
    semester INTEGER NOT NULL,
    gpa NUMERIC(4, 2) NOT NULL,
    credits_earned INTEGER NOT NULL,
    courses_count INTEGER NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (user_id, semester)
);

CREATE INDEX idx_reports_created_by ON reports(created_by);
CREATE INDEX idx_reports_type ON reports(report_type);
CREATE INDEX idx_kpi_values_period ON kpi_values(period);
CREATE INDEX idx_academic_performance_user_id ON academic_performance(user_id);
