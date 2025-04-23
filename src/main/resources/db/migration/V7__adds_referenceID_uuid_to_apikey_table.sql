
CREATE EXTENSION IF NOT EXISTS pgcrypto;

ALTER TABLE issued_api_key
    ADD COLUMN reference_id UUID;

UPDATE issued_api_key
SET reference_id = gen_random_uuid()
WHERE reference_id IS NULL;

ALTER TABLE issued_api_key
    ALTER COLUMN reference_id SET NOT NULL;

ALTER TABLE issued_api_key
    ADD CONSTRAINT unique_reference_id UNIQUE (reference_id);
