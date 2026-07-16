-- ENUM типы для ролей пользователей
CREATE TYPE user_role AS ENUM ('student', 'lecturer', 'department_head', 'partner', 'admin');

-- ENUM типы для типов уведомлений
CREATE TYPE notification_type AS ENUM ('task_assigned', 'deadline', 'project_invite', 'event_update', 'system', 'rating_update');

-- ENUM типы для типов ссылок в профиле
CREATE TYPE link_type AS ENUM ('github', 'linkedin', 'portfolio', 'website', 'cv', 'other');

-- ENUM типы для родства в экстренных контактах
CREATE TYPE relationship_type AS ENUM ('parent', 'spouse', 'sibling', 'friend', 'colleague', 'other');

-- ENUM типы для статуса сессии
CREATE TYPE session_status AS ENUM ('active', 'expired', 'revoked');

-- Основная таблица пользователей
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role user_role NOT NULL,
    is_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    last_login TIMESTAMPTZ,
    phone VARCHAR(50),
    address TEXT,
    birth_date DATE,
    faculty_id UUID,
    department_id UUID,
    study_program VARCHAR(255),
    group_name VARCHAR(50),
    enrollment_year INTEGER,
    current_semester INTEGER,
    avatar_url VARCHAR(512),
    bio TEXT,
    is_active BOOLEAN DEFAULT true
);

-- Информация о навыках пользователей
CREATE TABLE user_skills (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skill_name VARCHAR(100) NOT NULL,
    level INTEGER NOT NULL CHECK (level BETWEEN 1 AND 5),
    verified BOOLEAN DEFAULT false,
    PRIMARY KEY (user_id, skill_name)
);

-- Языки пользователей
CREATE TABLE user_languages (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    language VARCHAR(50) NOT NULL,
    proficiency VARCHAR(50) NOT NULL CHECK (proficiency IN ('beginner', 'intermediate', 'advanced', 'fluent', 'native')),
    PRIMARY KEY (user_id, language)
);

-- Ссылки профиля пользователя
CREATE TABLE user_links (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    link_type link_type NOT NULL,
    url VARCHAR(512) NOT NULL,
    PRIMARY KEY (user_id, link_type)
);

-- Экстренные контакты
CREATE TABLE emergency_contacts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    contact_name VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(50) NOT NULL,
    relationship relationship_type NOT NULL
);

-- Сессии пользователей
CREATE TABLE sessions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(512) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    ip_address VARCHAR(50),
    user_agent TEXT,
    status session_status DEFAULT 'active'
);

-- Настройки уведомлений
CREATE TABLE notification_settings (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    email_enabled BOOLEAN DEFAULT true,
    in_app_enabled BOOLEAN DEFAULT true,
    push_enabled BOOLEAN DEFAULT true,
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Уведомления пользователей
CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type notification_type NOT NULL,
    is_read BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    read_at TIMESTAMPTZ,
    related_entity_id UUID
);

-- Чаты
CREATE TABLE chats (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    project_id UUID,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    last_message_at TIMESTAMPTZ
);

-- Сообщения
CREATE TABLE messages (
    id UUID PRIMARY KEY,
    chat_id UUID NOT NULL REFERENCES chats(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    file_path VARCHAR(512),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Дашборды пользователей
CREATE TABLE dashboards (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    widget_config JSONB NOT NULL DEFAULT '[]'::jsonb,
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Индексы для оптимизации
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_department_id ON users(department_id);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_sessions_user_id ON sessions(user_id);
CREATE INDEX idx_sessions_token ON sessions(token);
CREATE INDEX idx_messages_chat_id ON messages(chat_id);
CREATE INDEX idx_messages_user_id ON messages(user_id);
CREATE INDEX idx_chats_project_id ON chats(project_id);