ALTER TABLE pairs ADD COLUMN IF NOT EXISTS updated_at timestamp without time zone;
UPDATE pairs SET updated_at = now() WHERE updated_at IS NULL;
ALTER TABLE pairs ALTER COLUMN updated_at SET NOT NULL;