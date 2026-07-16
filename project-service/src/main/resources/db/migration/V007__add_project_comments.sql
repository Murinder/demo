CREATE TABLE project_comments (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID         NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    author_id  UUID         NOT NULL,
    author_name VARCHAR(255) NOT NULL,
    author_role VARCHAR(50)  NOT NULL,
    content    TEXT         NOT NULL,
    created_at TIMESTAMPTZ  DEFAULT NOW()
);

CREATE INDEX idx_project_comments_project ON project_comments(project_id);
