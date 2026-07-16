-- Admin Service Schema

CREATE TYPE system_role AS ENUM ('ADMIN', 'DEPARTMENT_HEAD', 'LECTURER', 'STUDENT', 'PARTNER');
CREATE TYPE permission_type AS ENUM ('READ', 'WRITE', 'DELETE', 'ADMIN');
CREATE TYPE audit_action AS ENUM ('CREATE', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'IMPORT', 'EXPORT');

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role system_role NOT NULL,
    assigned_at TIMESTAMPTZ DEFAULT NOW(),
    assigned_by UUID,
    PRIMARY KEY (user_id, role)
);

CREATE TABLE role_permissions (
    role system_role NOT NULL,
    permission VARCHAR(100) NOT NULL,
    permission_type permission_type NOT NULL DEFAULT 'READ',
    resource VARCHAR(100),
    PRIMARY KEY (role, permission)
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    action audit_action NOT NULL,
    target VARCHAR(100),
    details JSONB,
    timestamp TIMESTAMPTZ DEFAULT NOW(),
    ip_address VARCHAR(50) NOT NULL,
    user_agent TEXT,
    before_state JSONB,
    after_state JSONB
);

CREATE TABLE system_settings (
    key VARCHAR(100) PRIMARY KEY,
    value TEXT NOT NULL,
    description TEXT,
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    updated_by UUID
);

CREATE TABLE data_operations (
    id UUID PRIMARY KEY,
    operation_type VARCHAR(50) NOT NULL CHECK (operation_type IN ('IMPORT', 'EXPORT', 'BACKUP', 'RESTORE')),
    entity_type VARCHAR(50) NOT NULL,
    file_path VARCHAR(512),
    status VARCHAR(50) NOT NULL CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')),
    started_at TIMESTAMPTZ DEFAULT NOW(),
    completed_at TIMESTAMPTZ,
    error_message TEXT,
    created_by UUID NOT NULL,
    record_count INTEGER DEFAULT 0,
    details JSONB
);

CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_data_operations_status ON data_operations(status);
