-- changeset David.Rabko:5_MED_210_Login
CREATE INDEX refresh_tokens_users__fk ON refresh_tokens (user_id);
-- rollback DROP INDEX refresh_tokens_users__fk;

-- changeset David.Rabko:6_MED_210_Login
CREATE INDEX verification_tokens_users__fk ON verification_tokens (user_id);
-- rollback DROP INDEX verification_tokens_users__fk;

-- changeset David.Rabko:7_MED_210_Login
CREATE INDEX users_roles__fk ON users (role);
-- rollback DROP INDEX users_roles__fk;
