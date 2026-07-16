-- Add project type column
ALTER TABLE projects ADD COLUMN IF NOT EXISTS project_type VARCHAR(50);

-- Seed demo project types
UPDATE projects SET project_type = 'DIPLOMA' WHERE id = '60000000-0000-0000-0000-000000000001';
UPDATE projects SET project_type = 'COURSEWORK' WHERE id = '60000000-0000-0000-0000-000000000002';
UPDATE projects SET project_type = 'RESEARCH' WHERE id = '60000000-0000-0000-0000-000000000003';
