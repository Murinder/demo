-- Fix user_languages proficiency check constraint to match Java enum (uppercase)
ALTER TABLE user_languages DROP CONSTRAINT IF EXISTS user_languages_proficiency_check;
ALTER TABLE user_languages ADD CONSTRAINT user_languages_proficiency_check
    CHECK (proficiency IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'FLUENT', 'NATIVE'));
