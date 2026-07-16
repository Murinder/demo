-- Rename old ENUM types
ALTER TYPE project_status RENAME TO project_status_old;
ALTER TYPE task_status RENAME TO task_status_old;
ALTER TYPE project_role RENAME TO project_role_old;
ALTER TYPE template_type RENAME TO template_type_old;

-- Create new ENUM types with uppercase values
CREATE TYPE project_status AS ENUM ('ACTIVE', 'COMPLETED', 'FROZEN', 'CANCELLED');
CREATE TYPE task_status AS ENUM ('TO_DO', 'IN_PROGRESS', 'REVIEW', 'DONE', 'BLOCKED');
CREATE TYPE project_role AS ENUM ('LEADER', 'MEMBER', 'MENTOR', 'OBSERVER');
CREATE TYPE template_type AS ENUM ('DIPLOMA', 'RESEARCH', 'COMMERCIAL_CASE', 'HACKATHON', 'OTHER');

-- Update table columns to use new ENUM types and cast values to uppercase
ALTER TABLE projects ALTER COLUMN status DROP DEFAULT;
ALTER TABLE projects ALTER COLUMN status SET DATA TYPE project_status USING UPPER(status::text)::project_status;
ALTER TABLE projects ALTER COLUMN status SET DEFAULT 'ACTIVE';

ALTER TABLE tasks ALTER COLUMN status DROP DEFAULT;
ALTER TABLE tasks ALTER COLUMN status SET DATA TYPE task_status USING UPPER(status::text)::task_status;
ALTER TABLE tasks ALTER COLUMN status SET DEFAULT 'TO_DO';

ALTER TABLE project_members ALTER COLUMN role DROP DEFAULT;
ALTER TABLE project_members ALTER COLUMN role SET DATA TYPE project_role USING UPPER(role::text)::project_role;
ALTER TABLE project_members ALTER COLUMN role SET DEFAULT 'MEMBER';

ALTER TABLE project_templates ALTER COLUMN template_type SET DATA TYPE template_type USING UPPER(template_type::text)::template_type;

-- Drop old ENUM types
DROP TYPE project_status_old;
DROP TYPE task_status_old;
DROP TYPE project_role_old;
DROP TYPE template_type_old;