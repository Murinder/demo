-- Миграция для расширения ENUM user_role и перевода всех значений в верхний регистр
DO $$
DECLARE
    old_roles text[] := ARRAY['student', 'lecturer', 'department_head', 'partner', 'admin'];
    new_roles text[] := ARRAY['STUDENT', 'LECTURER', 'DEPARTMENT_HEAD', 'PARTNER', 'ADMIN'];
BEGIN
    -- 1. Переименовать старый ENUM
    ALTER TYPE user_role RENAME TO user_role_old;

    -- 2. Создать новый ENUM с нужными значениями
    CREATE TYPE user_role AS ENUM ('STUDENT', 'LECTURER', 'DEPARTMENT_HEAD', 'PARTNER', 'ADMIN');

    -- 3. Преобразовать значения в таблице users
    ALTER TABLE users ALTER COLUMN role TYPE text;
    UPDATE users SET role = UPPER(role);
    ALTER TABLE users ALTER COLUMN role TYPE user_role USING role::user_role;

    -- 4. Удалить старый ENUM
    DROP TYPE user_role_old;
END $$;