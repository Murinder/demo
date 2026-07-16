-- Remove erroneous uniqueness constraint that prevented students
-- from creating multiple applications of the same type
DROP INDEX IF EXISTS uq_pending_application;
