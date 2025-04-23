
CREATE TABLE IF NOT EXISTS account_roles (
                                             account_id BIGINT NOT NULL,
                                             role VARCHAR(255) NOT NULL DEFAULT 'ROLE_USER',
    FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_account_roles_account_id ON account_roles(account_id);
