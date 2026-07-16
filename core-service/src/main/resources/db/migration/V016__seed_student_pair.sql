-- =============================================
-- V016: Два тестовых студента для проверки
-- совместной видимости задач в проекте
-- =============================================

-- Студент 1
INSERT INTO users (id, email, password_hash, first_name, last_name, role, is_verified, is_active,
                   phone, birth_date, bio, study_program, group_name, enrollment_year, current_semester,
                   faculty_id, department_id)
VALUES
    ('00000000-0000-0000-0000-000000000004', 'student1@university.edu',
     '$2b$10$ZL/KmZ4Vpi07bfRmsXrBceuGDLC7jsRujtvF3yx8RUYQE1fa9wR.6',
     'Пётр', 'Первый', 'STUDENT'::user_role, true, true,
     '+7 (999) 444-44-44', '2003-02-10',
     'Студент 3-го курса, занимается веб-разработкой и бэкенд-системами.',
     'Программная инженерия', 'ИТ-21', 2023, 4,
     '10000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001');

-- Студент 2
INSERT INTO users (id, email, password_hash, first_name, last_name, role, is_verified, is_active,
                   phone, birth_date, bio, study_program, group_name, enrollment_year, current_semester,
                   faculty_id, department_id)
VALUES
    ('00000000-0000-0000-0000-000000000005', 'student2@university.edu',
     '$2b$10$ZL/KmZ4Vpi07bfRmsXrBceuGDLC7jsRujtvF3yx8RUYQE1fa9wR.6',
     'Анна', 'Вторая', 'STUDENT'::user_role, true, true,
     '+7 (999) 555-55-55', '2003-08-22',
     'Студентка 3-го курса, интересуется фронтенд-разработкой и UX-дизайном.',
     'Программная инженерия', 'ИТ-21', 2023, 4,
     '10000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001');

-- Навыки студента 1
INSERT INTO user_skills (user_id, skill_name, level, verified) VALUES
    ('00000000-0000-0000-0000-000000000004', 'Java', 3, false),
    ('00000000-0000-0000-0000-000000000004', 'Spring Boot', 2, false),
    ('00000000-0000-0000-0000-000000000004', 'PostgreSQL', 3, false);

-- Навыки студента 2
INSERT INTO user_skills (user_id, skill_name, level, verified) VALUES
    ('00000000-0000-0000-0000-000000000005', 'React', 3, false),
    ('00000000-0000-0000-0000-000000000005', 'TypeScript', 3, false),
    ('00000000-0000-0000-0000-000000000005', 'Figma', 4, false);

-- Языки студента 1
INSERT INTO user_languages (user_id, language, proficiency) VALUES
    ('00000000-0000-0000-0000-000000000004', 'Русский', 'NATIVE'),
    ('00000000-0000-0000-0000-000000000004', 'Английский', 'INTERMEDIATE');

-- Языки студента 2
INSERT INTO user_languages (user_id, language, proficiency) VALUES
    ('00000000-0000-0000-0000-000000000005', 'Русский', 'NATIVE'),
    ('00000000-0000-0000-0000-000000000005', 'Английский', 'ADVANCED');
