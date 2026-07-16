ALTER TABLE reports ALTER COLUMN report_type TYPE VARCHAR(100) USING report_type::text;
DROP TYPE IF EXISTS report_type;
