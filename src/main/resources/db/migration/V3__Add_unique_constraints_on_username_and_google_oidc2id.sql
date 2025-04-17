ALTER TABLE account
    ADD google_oidc2id VARCHAR(255);

UPDATE account
SET google_oidc2id = account_id::TEXT
WHERE google_oidc2id IS NULL;

ALTER TABLE account
    ALTER COLUMN google_oidc2id SET NOT NULL;

ALTER TABLE account
    ADD CONSTRAINT uc_account_googleoidc2id UNIQUE (google_oidc2id);


CREATE UNIQUE INDEX idx_account_google_oidc2_id ON account (google_oidc2id);
