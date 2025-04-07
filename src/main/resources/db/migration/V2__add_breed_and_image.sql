ALTER TABLE account
    ADD breed VARCHAR(255) NOT NULL DEFAULT 'German Shepherd';

ALTER TABLE account
    ADD image BYTEA;