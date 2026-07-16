-- Document Service Schema

CREATE TYPE document_type AS ENUM ('CERTIFICATE', 'DIPLOMA', 'REPORT', 'AGREEMENT', 'RECOMMENDATION', 'GRANT_APPLICATION');
CREATE TYPE template_status AS ENUM ('DRAFT', 'ACTIVE', 'ARCHIVED');
CREATE TYPE placeholder_type AS ENUM ('USER', 'PROJECT', 'EVENT', 'SYSTEM', 'DEPARTMENT', 'ACADEMIC');

CREATE TABLE document_templates (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    file_path VARCHAR(512) NOT NULL,
    document_type document_type NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    status template_status DEFAULT 'DRAFT',
    version INTEGER NOT NULL DEFAULT 1,
    is_public BOOLEAN DEFAULT false,
    faculty_id UUID,
    department_id UUID
);

CREATE TABLE placeholders (
    template_id UUID NOT NULL REFERENCES document_templates(id) ON DELETE CASCADE,
    placeholder VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    data_type VARCHAR(50) NOT NULL CHECK (data_type IN ('STRING', 'DATE', 'NUMBER', 'BOOLEAN', 'ARRAY')),
    placeholder_type placeholder_type NOT NULL,
    example_value TEXT,
    is_required BOOLEAN DEFAULT false,
    PRIMARY KEY (template_id, placeholder)
);

CREATE TABLE generated_documents (
    id UUID PRIMARY KEY,
    template_id UUID NOT NULL REFERENCES document_templates(id) ON DELETE CASCADE,
    generated_for UUID NOT NULL,
    file_path VARCHAR(512) NOT NULL,
    generated_at TIMESTAMPTZ DEFAULT NOW(),
    generated_by UUID NOT NULL,
    parameters JSONB NOT NULL DEFAULT '{}'::jsonb,
    status VARCHAR(50) NOT NULL DEFAULT 'COMPLETED' CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')),
    error_message TEXT,
    signed_at TIMESTAMPTZ,
    expires_at TIMESTAMPTZ
);

CREATE TABLE document_signatures (
    document_id UUID NOT NULL REFERENCES generated_documents(id) ON DELETE CASCADE,
    signer_id UUID NOT NULL,
    signed_at TIMESTAMPTZ DEFAULT NOW(),
    signature_type VARCHAR(50) NOT NULL CHECK (signature_type IN ('ELECTRONIC', 'HANDWRITTEN', 'APPROVED')),
    ip_address VARCHAR(50),
    user_agent TEXT,
    comment TEXT,
    PRIMARY KEY (document_id, signer_id)
);

CREATE INDEX idx_document_templates_type ON document_templates(document_type);
CREATE INDEX idx_generated_documents_template_id ON generated_documents(template_id);
CREATE INDEX idx_generated_documents_for ON generated_documents(generated_for);
