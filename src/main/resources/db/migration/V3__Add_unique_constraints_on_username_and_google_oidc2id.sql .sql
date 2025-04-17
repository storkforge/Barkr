-- Step 1: Add the column as nullable first
ALTER TABLE account
    ADD google_oidc2id VARCHAR(255);

-- Step 2: Set a default value for existing rows using the primary key
UPDATE account
SET google_oidc2id = account_id::TEXT
WHERE google_oidc2id IS NULL;

-- Step 3: Set the column as NOT NULL
ALTER TABLE account
    ALTER COLUMN google_oidc2id SET NOT NULL;

-- Ensure the google_oidc2id column is unique
ALTER TABLE account
    ADD CONSTRAINT uc_account_googleoidc2id UNIQUE (google_oidc2id);


-- Add a unique index for google_oidc2id for performance optimization
CREATE UNIQUE INDEX idx_account_google_oidc2_id ON account (google_oidc2id);
