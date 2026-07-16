-- Extend users table with head-specific academic fields
ALTER TABLE users ADD COLUMN IF NOT EXISTS academic_title VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS head_since DATE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS dissertation_title TEXT;
ALTER TABLE users ADD COLUMN IF NOT EXISTS dissertation_year INTEGER;
ALTER TABLE users ADD COLUMN IF NOT EXISTS education_history JSONB;

-- Extend lecturer_academic_metrics with breakdown fields
ALTER TABLE lecturer_academic_metrics ADD COLUMN IF NOT EXISTS monographs INTEGER DEFAULT 0;
ALTER TABLE lecturer_academic_metrics ADD COLUMN IF NOT EXISTS articles INTEGER DEFAULT 0;
ALTER TABLE lecturer_academic_metrics ADD COLUMN IF NOT EXISTS conferences INTEGER DEFAULT 0;
ALTER TABLE lecturer_academic_metrics ADD COLUMN IF NOT EXISTS teaching_start_year INTEGER;
ALTER TABLE lecturer_academic_metrics ADD COLUMN IF NOT EXISTS supervised_phd INTEGER DEFAULT 0;
ALTER TABLE lecturer_academic_metrics ADD COLUMN IF NOT EXISTS supervised_masters INTEGER DEFAULT 0;
ALTER TABLE lecturer_academic_metrics ADD COLUMN IF NOT EXISTS supervised_bachelors INTEGER DEFAULT 0;

-- Seed demo data for head user
UPDATE users SET
    degree = 'Доктор технических наук',
    academic_title = 'Профессор',
    head_since = '2015-09-01',
    dissertation_title = 'Методы оптимизации распределенных систем обработки данных',
    dissertation_year = 2008,
    education_history = '[{"degree":"Доктор технических наук","university":"Московский государственный университет","program":"Информатика и вычислительная техника","year":"2008"},{"degree":"Кандидат технических наук","university":"Московский государственный университет","program":"Математическое моделирование","year":"1998"}]'::jsonb
WHERE id = '00000000-0000-0000-0000-000000000003';

UPDATE lecturer_academic_metrics SET
    monographs = 4,
    articles = 98,
    conferences = 54,
    teaching_start_year = 1998,
    supervised_phd = 12,
    supervised_masters = 45,
    supervised_bachelors = 156
WHERE user_id = '00000000-0000-0000-0000-000000000003';
