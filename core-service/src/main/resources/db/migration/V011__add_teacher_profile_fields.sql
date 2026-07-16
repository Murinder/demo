-- Add teacher-specific profile fields
ALTER TABLE users ADD COLUMN IF NOT EXISTS position VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS degree VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS teacher_id VARCHAR(50);
ALTER TABLE users ADD COLUMN IF NOT EXISTS experience VARCHAR(50);
ALTER TABLE users ADD COLUMN IF NOT EXISTS office VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS office_hours VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS website VARCHAR(512);
ALTER TABLE users ADD COLUMN IF NOT EXISTS linkedin VARCHAR(512);

-- Seed demo teacher data
UPDATE users SET
    position = 'Доцент',
    degree = 'Кандидат технических наук',
    teacher_id = 'T-2018-567',
    experience = '15 лет',
    office = 'Главный корпус, кабинет 401',
    office_hours = 'Вторник, Четверг 14:00-16:00',
    website = 'https://petrov.edu',
    linkedin = 'https://linkedin.com/in/petrov',
    bio = 'Преподаватель с 15-летним стажем. Специализируюсь на разработке программного обеспечения и веб-технологиях. Активно занимаюсь научной работой в области искусственного интеллекта и машинного обучения.'
WHERE id = '00000000-0000-0000-0000-000000000002';
