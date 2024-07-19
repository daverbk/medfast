-- changeset David.Rabko:11_MED_210_Login
INSERT INTO roles (name)
VALUES ('PATIENT'),
       ('DOCTOR'),
       ('ADMIN');
-- rollback DELETE FROM roles WHERE name IN ('PATIENT', 'DOCTOR', 'ADMIN');
