-- For event_format
ALTER TYPE event_format RENAME TO event_format_old;
CREATE TYPE event_format AS ENUM ('ONLINE', 'OFFLINE', 'HYBRID');
ALTER TABLE events ALTER COLUMN format TYPE event_format USING format::text::event_format;
DROP TYPE event_format_old;

-- For application_status
ALTER TABLE event_applications ALTER COLUMN status DROP DEFAULT;
ALTER TYPE application_status RENAME TO application_status_old;
CREATE TYPE application_status AS ENUM ('SUBMITTED', 'APPROVED', 'REJECTED', 'WAITLISTED');
ALTER TABLE event_applications ALTER COLUMN status TYPE application_status USING status::text::application_status;
ALTER TABLE event_applications ALTER COLUMN status SET DEFAULT 'SUBMITTED';
DROP TYPE application_status_old;

-- For event_status
ALTER TABLE events ALTER COLUMN status DROP DEFAULT;
ALTER TYPE event_status RENAME TO event_status_old;
CREATE TYPE event_status AS ENUM ('DRAFT', 'PUBLISHED', 'REGISTRATION_OPEN', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');
ALTER TABLE events ALTER COLUMN status TYPE event_status USING status::text::event_status;
ALTER TABLE events ALTER COLUMN status SET DEFAULT 'DRAFT';
DROP TYPE event_status_old;