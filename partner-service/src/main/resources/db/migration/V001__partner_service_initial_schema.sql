-- Partner Service Schema

-- ENUM types
CREATE TYPE partnership_status AS ENUM ('PROSPECTIVE', 'ACTIVE', 'INACTIVE', 'TERMINATED');
CREATE TYPE vacancy_type AS ENUM ('INTERNSHIP', 'FULL_TIME', 'PART_TIME', 'PROJECT');
CREATE TYPE agreement_status AS ENUM ('DRAFT', 'SENT', 'SIGNED', 'EXPIRED', 'REVOKED');

-- Tables
CREATE TABLE partners (
    id UUID PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    contact_info TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    is_active BOOLEAN DEFAULT true,
    website VARCHAR(512),
    industry VARCHAR(100),
    partnership_status partnership_status DEFAULT 'PROSPECTIVE',
    logo_url VARCHAR(512),
    description TEXT
);

CREATE TABLE partner_contacts (
    id UUID PRIMARY KEY,
    partner_id UUID NOT NULL REFERENCES partners(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    position VARCHAR(100),
    email VARCHAR(255),
    phone VARCHAR(50),
    is_primary BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE cases (
    id UUID PRIMARY KEY,
    partner_id UUID NOT NULL REFERENCES partners(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    difficulty VARCHAR(50) CHECK (difficulty IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),
    required_skills TEXT[],
    reward_description TEXT,
    is_active BOOLEAN DEFAULT true,
    expected_duration VARCHAR(100)
);

CREATE TABLE vacancies (
    id UUID PRIMARY KEY,
    partner_id UUID NOT NULL REFERENCES partners(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    requirements TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    is_active BOOLEAN DEFAULT true,
    vacancy_type vacancy_type NOT NULL,
    location VARCHAR(255),
    salary_range VARCHAR(100),
    application_deadline DATE,
    contact_id UUID REFERENCES partner_contacts(id)
);

CREATE TABLE agreements (
    id UUID PRIMARY KEY,
    partner_id UUID NOT NULL REFERENCES partners(id) ON DELETE CASCADE,
    project_id UUID NOT NULL,
    document_path VARCHAR(512) NOT NULL,
    status agreement_status NOT NULL DEFAULT 'DRAFT',
    signed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    expires_at TIMESTAMPTZ,
    version INTEGER NOT NULL DEFAULT 1,
    description TEXT
);

-- Indexes
CREATE INDEX idx_partners_company_name ON partners(company_name);
CREATE INDEX idx_partner_contacts_partner_id ON partner_contacts(partner_id);
CREATE INDEX idx_cases_partner_id ON cases(partner_id);
CREATE INDEX idx_vacancies_partner_id ON vacancies(partner_id);
CREATE INDEX idx_agreements_partner_id ON agreements(partner_id);
CREATE INDEX idx_agreements_status ON agreements(status);
