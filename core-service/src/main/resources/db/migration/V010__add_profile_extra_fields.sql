-- Add study form, emergency contact, and interests fields to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS study_form VARCHAR(50);
ALTER TABLE users ADD COLUMN IF NOT EXISTS emergency_contact_name VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS emergency_contact_phone VARCHAR(50);
ALTER TABLE users ADD COLUMN IF NOT EXISTS interests TEXT;

-- Set default study form for existing students
UPDATE users SET study_form = 'Очная' WHERE role = 'STUDENT' AND study_form IS NULL;

-- Set demo data for test student
UPDATE users
SET emergency_contact_name = 'Студентова Елена Павловна',
    emergency_contact_phone = '+7 (999) 222-33-44',
    interests = 'Машинное обучение,Веб-разработка,Мобильные приложения,Open Source'
WHERE email = 'student@university.edu';
