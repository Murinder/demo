-- Portfolio Service Schema

-- ENUM types
CREATE TYPE achievement_type AS ENUM ('PROJECT', 'EVENT', 'CERTIFICATE', 'PUBLICATION', 'GRANT', 'EXTERNAL');
CREATE TYPE verification_status AS ENUM ('PENDING', 'VERIFIED', 'REJECTED');
CREATE TYPE review_type AS ENUM ('PROJECT', 'EVENT', 'MENTORSHIP');

-- Tables
CREATE TABLE portfolios (
    user_id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    visibility_settings JSONB
);

CREATE TABLE achievements (
    id UUID PRIMARY KEY,
    portfolio_id UUID NOT NULL REFERENCES portfolios(user_id) ON DELETE CASCADE,
    type achievement_type NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    date DATE,
    proof_document VARCHAR(512),
    is_external BOOLEAN DEFAULT false,
    issuer VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE skills (
    id UUID PRIMARY KEY,
    portfolio_id UUID NOT NULL REFERENCES portfolios(user_id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    level INT NOT NULL CHECK (level >= 1 AND level <= 5),
    verification_status verification_status DEFAULT 'PENDING',
    verified_by UUID,
    verified_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    portfolio_id UUID NOT NULL REFERENCES portfolios(user_id) ON DELETE CASCADE,
    from_user_id UUID NOT NULL,
    content TEXT,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    type review_type NOT NULL,
    related_entity_id UUID,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE academic_info (
    portfolio_id UUID PRIMARY KEY REFERENCES portfolios(user_id) ON DELETE CASCADE,
    faculty VARCHAR(255),
    department VARCHAR(255),
    study_program VARCHAR(255),
    group_name VARCHAR(50),
    enrollment_year INTEGER,
    current_semester INTEGER,
    gpa NUMERIC(4,2),
    credits_earned INTEGER,
    progress_percentage NUMERIC(5,2),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_achievements_portfolio_id ON achievements(portfolio_id);
CREATE INDEX idx_skills_portfolio_id ON skills(portfolio_id);
CREATE INDEX idx_reviews_portfolio_id ON reviews(portfolio_id);
CREATE INDEX idx_skills_name ON skills(name);