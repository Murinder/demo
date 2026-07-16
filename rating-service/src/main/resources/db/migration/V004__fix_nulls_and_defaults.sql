-- =============================================================
-- V004: Fix NULL values and set defaults
-- =============================================================

-- Fix NULL calculated_at in existing history records
UPDATE rating_history SET calculated_at = NOW() WHERE calculated_at IS NULL;

-- Fix NULL verification_status in existing student_ratings
UPDATE student_ratings SET verification_status = 'PENDING' WHERE verification_status IS NULL;

-- Fix NULL verification_status in existing lecturer_ratings
UPDATE lecturer_ratings SET verification_status = 'PENDING' WHERE verification_status IS NULL;

-- Set default for calculated_at so future inserts always have a timestamp
ALTER TABLE rating_history ALTER COLUMN calculated_at SET DEFAULT NOW();

-- Set default for verification_status
ALTER TABLE student_ratings ALTER COLUMN verification_status SET DEFAULT 'PENDING';
ALTER TABLE lecturer_ratings ALTER COLUMN verification_status SET DEFAULT 'PENDING';
