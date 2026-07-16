-- ENUM типы для статуса проектов
CREATE TYPE project_status AS ENUM ('active', 'completed', 'frozen', 'cancelled');

-- ENUM типы для статуса задач
CREATE TYPE task_status AS ENUM ('to_do', 'in_progress', 'review', 'done', 'blocked');

-- ENUM типы для ролей участников проекта
CREATE TYPE project_role AS ENUM ('leader', 'member', 'mentor', 'observer');

-- ENUM типы для типов шаблонов проектов
CREATE TYPE template_type AS ENUM ('diploma', 'research', 'commercial_case', 'hackathon', 'other');

-- Проекты
CREATE TABLE projects (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    template_id UUID,
    status project_status NOT NULL DEFAULT 'active',
    start_date DATE,
    end_date DATE,
    created_by UUID NOT NULL,
    department_id UUID,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Индексы для projects
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_projects_created_by ON projects(created_by);

-- Участники проектов
CREATE TABLE project_members (
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id UUID NOT NULL,
    role project_role NOT NULL DEFAULT 'member',
    joined_at TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (project_id, user_id)
);

-- Индексы для project_members
CREATE INDEX idx_project_members_user_id ON project_members(user_id);

-- Задачи проектов
CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status task_status NOT NULL DEFAULT 'to_do',
    assigned_to UUID,
    due_date DATE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Индексы для tasks
CREATE INDEX idx_tasks_project_id ON tasks(project_id);
CREATE INDEX idx_tasks_assigned_to ON tasks(assigned_to);

-- Документы проектов
CREATE TABLE project_documents (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    task_id UUID,
    file_path VARCHAR(512) NOT NULL,
    version INT NOT NULL DEFAULT 1,
    uploaded_by UUID NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    description TEXT
);

-- Индексы для project_documents
CREATE INDEX idx_project_documents_project_id ON project_documents(project_id);

-- Шаблоны проектов
CREATE TABLE project_templates (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    template_type template_type NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    config JSONB NOT NULL DEFAULT '{}'::jsonb
);