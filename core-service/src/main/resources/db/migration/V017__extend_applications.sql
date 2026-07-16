-- Extend applications: new kinds, statuses, comments, auto-numbering

-- New application_kind values
ALTER TYPE application_kind ADD VALUE IF NOT EXISTS 'RESOURCE_REQUEST';
ALTER TYPE application_kind ADD VALUE IF NOT EXISTS 'EQUIPMENT_REQUEST';
ALTER TYPE application_kind ADD VALUE IF NOT EXISTS 'ROOM_REQUEST';
ALTER TYPE application_kind ADD VALUE IF NOT EXISTS 'OTHER';

-- New application_status values
ALTER TYPE application_status ADD VALUE IF NOT EXISTS 'REVISION';
ALTER TYPE application_status ADD VALUE IF NOT EXISTS 'ADMIN_REVIEW';
ALTER TYPE application_status ADD VALUE IF NOT EXISTS 'IN_PROGRESS';
ALTER TYPE application_status ADD VALUE IF NOT EXISTS 'COMPLETED';
ALTER TYPE application_status ADD VALUE IF NOT EXISTS 'WITHDRAWN';

-- Auto-incrementing application number for display name "Заявка №XXX"
ALTER TABLE applications ADD COLUMN IF NOT EXISTS application_number SERIAL;

-- Admin (department head) who handles the second approval stage
ALTER TABLE applications ADD COLUMN IF NOT EXISTS admin_id UUID REFERENCES users(id);

-- Make title nullable for new application kinds (auto-generated)
ALTER TABLE applications ALTER COLUMN title DROP NOT NULL;

-- Comments table for application conversation (revision requests, rejections, etc.)
CREATE TABLE IF NOT EXISTS application_comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    author_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    author_role VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_app_comments_application ON application_comments(application_id);
