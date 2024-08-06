-- changeset David.Rabko:11_MED_210_Login
INSERT INTO roles (name)
VALUES ('PATIENT'),
       ('DOCTOR'),
       ('ADMIN');
-- rollback DELETE FROM roles WHERE name IN ('PATIENT', 'DOCTOR', 'ADMIN');

-- changeset Uladzislau.Lukashevich:33_MED_144_Appointment
INSERT INTO appointment_statuses (status)
VALUES ('SCHEDULED'),
       ('SCHEDULED_CONFIRMED'),
       ('CANCELLED_PATIENT'),
       ('CANCELLED_CLINIC'),
       ('IN_CONSULTATION'),
       ('COMPLETED'),
       ('MISSED')
-- rollback DELETE FROM appointment_statuses WHERE status IN ('SCHEDULED',
--        'SCHEDULED_CONFIRMED',
--        'CANCELLED_PATIENT',
--        'CANCELLED_CLINIC',
--        'IN_CONSULTATION',
--        'COMPLETED',
--        'MISSED');
