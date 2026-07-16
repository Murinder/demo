-- =============================================================
-- V002: Add unique constraints to prevent duplicate entities
-- =============================================================

-- 1. Partner company name must be unique
DELETE FROM partners a
    USING partners b
    WHERE a.company_name = b.company_name
      AND a.created_at > b.created_at;

ALTER TABLE partners
    ADD CONSTRAINT uq_partner_company_name UNIQUE (company_name);

-- 2. Contact email must be unique per partner
DELETE FROM partner_contacts a
    USING partner_contacts b
    WHERE a.partner_id = b.partner_id
      AND a.email = b.email
      AND a.email IS NOT NULL
      AND a.created_at > b.created_at;

ALTER TABLE partner_contacts
    ADD CONSTRAINT uq_partner_contact_email UNIQUE (partner_id, email);
