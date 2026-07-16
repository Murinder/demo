-- notification_type
ALTER TYPE notification_type RENAME TO notification_type_old;
CREATE TYPE notification_type AS ENUM ('TASK_ASSIGNED', 'DEADLINE', 'PROJECT_INVITE', 'EVENT_UPDATE', 'SYSTEM', 'RATING_UPDATE');
ALTER TABLE notifications ALTER COLUMN type TYPE notification_type USING type::text::notification_type;
DROP TYPE notification_type_old;

-- link_type
ALTER TYPE link_type RENAME TO link_type_old;
CREATE TYPE link_type AS ENUM ('GITHUB', 'LINKEDIN', 'PORTFOLIO', 'WEBSITE', 'CV', 'OTHER');
ALTER TABLE user_links ALTER COLUMN link_type TYPE link_type USING link_type::text::link_type;
DROP TYPE link_type_old;

-- relationship_type
ALTER TYPE relationship_type RENAME TO relationship_type_old;
CREATE TYPE relationship_type AS ENUM ('PARENT', 'SPOUSE', 'SIBLING', 'FRIEND', 'COLLEAGUE', 'OTHER');
ALTER TABLE emergency_contacts ALTER COLUMN relationship TYPE relationship_type USING relationship::text::relationship_type;
DROP TYPE relationship_type_old;

-- session_status
ALTER TABLE sessions ALTER COLUMN status DROP DEFAULT;
ALTER TYPE session_status RENAME TO session_status_old;
CREATE TYPE session_status AS ENUM ('ACTIVE', 'EXPIRED', 'REVOKED');
ALTER TABLE sessions ALTER COLUMN status TYPE session_status USING status::text::session_status;
ALTER TABLE sessions ALTER COLUMN status SET DEFAULT 'ACTIVE';
DROP TYPE session_status_old;