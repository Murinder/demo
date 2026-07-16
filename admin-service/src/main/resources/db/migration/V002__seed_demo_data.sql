-- ============================================================
-- SEED DEMO DATA: admin-service
-- ============================================================

-- User UUIDs (from core-service)
-- STUDENT:  00000000-0000-0000-0000-000000000001
-- TEACHER:  00000000-0000-0000-0000-000000000002
-- HEAD:     00000000-0000-0000-0000-000000000003

-- ============================================================
-- 1. USER ROLES
-- ============================================================
INSERT INTO user_roles (user_id, role, assigned_by) VALUES
    ('00000000-0000-0000-0000-000000000001', 'STUDENT'::system_role, '00000000-0000-0000-0000-000000000003'),
    ('00000000-0000-0000-0000-000000000002', 'LECTURER'::system_role, '00000000-0000-0000-0000-000000000003'),
    ('00000000-0000-0000-0000-000000000003', 'DEPARTMENT_HEAD'::system_role, '00000000-0000-0000-0000-000000000003')
ON CONFLICT (user_id, role) DO NOTHING;

-- ============================================================
-- 2. ROLE PERMISSIONS
-- ============================================================
INSERT INTO role_permissions (role, permission, permission_type, resource) VALUES
    -- STUDENT permissions
    ('STUDENT'::system_role, 'projects.read', 'READ'::permission_type, 'projects'),
    ('STUDENT'::system_role, 'events.read', 'READ'::permission_type, 'events'),
    ('STUDENT'::system_role, 'rating.read', 'READ'::permission_type, 'rating'),
    ('STUDENT'::system_role, 'portfolio.read', 'READ'::permission_type, 'portfolio'),
    ('STUDENT'::system_role, 'portfolio.write', 'WRITE'::permission_type, 'portfolio'),
    ('STUDENT'::system_role, 'reports.read', 'READ'::permission_type, 'reports'),

    -- LECTURER permissions
    ('LECTURER'::system_role, 'projects.read', 'READ'::permission_type, 'projects'),
    ('LECTURER'::system_role, 'projects.write', 'WRITE'::permission_type, 'projects'),
    ('LECTURER'::system_role, 'events.read', 'READ'::permission_type, 'events'),
    ('LECTURER'::system_role, 'events.write', 'WRITE'::permission_type, 'events'),
    ('LECTURER'::system_role, 'rating.read', 'READ'::permission_type, 'rating'),
    ('LECTURER'::system_role, 'reports.write', 'WRITE'::permission_type, 'reports'),

    -- DEPARTMENT_HEAD permissions
    ('DEPARTMENT_HEAD'::system_role, 'projects.read', 'READ'::permission_type, 'projects'),
    ('DEPARTMENT_HEAD'::system_role, 'projects.write', 'WRITE'::permission_type, 'projects'),
    ('DEPARTMENT_HEAD'::system_role, 'events.read', 'READ'::permission_type, 'events'),
    ('DEPARTMENT_HEAD'::system_role, 'events.write', 'WRITE'::permission_type, 'events'),
    ('DEPARTMENT_HEAD'::system_role, 'rating.read', 'READ'::permission_type, 'rating'),
    ('DEPARTMENT_HEAD'::system_role, 'rating.write', 'WRITE'::permission_type, 'rating'),
    ('DEPARTMENT_HEAD'::system_role, 'admin.read', 'READ'::permission_type, 'admin'),
    ('DEPARTMENT_HEAD'::system_role, 'reports.write', 'WRITE'::permission_type, 'reports'),
    ('DEPARTMENT_HEAD'::system_role, 'reports.read', 'READ'::permission_type, 'reports'),

    -- ADMIN permissions (full access)
    ('ADMIN'::system_role, 'admin.full', 'ADMIN'::permission_type, 'admin'),
    ('ADMIN'::system_role, 'projects.full', 'ADMIN'::permission_type, 'projects'),
    ('ADMIN'::system_role, 'events.full', 'ADMIN'::permission_type, 'events'),
    ('ADMIN'::system_role, 'rating.full', 'ADMIN'::permission_type, 'rating'),
    ('ADMIN'::system_role, 'reports.full', 'ADMIN'::permission_type, 'reports'),
    ('ADMIN'::system_role, 'users.full', 'ADMIN'::permission_type, 'users')
ON CONFLICT (role, permission) DO NOTHING;

-- ============================================================
-- 3. SYSTEM SETTINGS
-- ============================================================
INSERT INTO system_settings (key, value, description, updated_by) VALUES
    ('platform.name', 'ЕЦОПУ', 'Название платформы', '00000000-0000-0000-0000-000000000003'),
    ('semester.current', '2', 'Текущий семестр', '00000000-0000-0000-0000-000000000003'),
    ('academic.year', '2025-2026', 'Текущий учебный год', '00000000-0000-0000-0000-000000000003')
ON CONFLICT (key) DO NOTHING;
