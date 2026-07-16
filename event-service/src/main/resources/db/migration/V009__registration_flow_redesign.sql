-- Add PENDING_TEAM_APPROVAL to application_status enum
ALTER TABLE event_applications ALTER COLUMN status DROP DEFAULT;
ALTER TYPE application_status RENAME TO application_status_old;
CREATE TYPE application_status AS ENUM ('SUBMITTED','APPROVED','REJECTED','WAITLISTED','PENDING_TEAM_APPROVAL');
ALTER TABLE event_applications ALTER COLUMN status TYPE application_status USING status::text::application_status;
ALTER TABLE event_applications ALTER COLUMN status SET DEFAULT 'SUBMITTED';
DROP TYPE application_status_old;

-- New fields for registration flow
ALTER TABLE event_applications ADD COLUMN participant_role VARCHAR(50);
ALTER TABLE event_applications ADD COLUMN presentation_title VARCHAR(255);
ALTER TABLE event_applications ADD COLUMN presentation_description TEXT;
