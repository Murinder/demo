CREATE TABLE lecturer_academic_metrics (
    user_id       UUID PRIMARY KEY REFERENCES users(id),
    publications  INTEGER NOT NULL DEFAULT 0,
    grants        INTEGER NOT NULL DEFAULT 0,
    hours         INTEGER NOT NULL DEFAULT 0,
    consultations INTEGER NOT NULL DEFAULT 0,
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
