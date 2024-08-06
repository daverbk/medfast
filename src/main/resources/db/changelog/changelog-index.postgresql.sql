-- changeset David.Rabko:5_MED_210_Login
CREATE INDEX refresh_tokens_users__fk ON refresh_tokens (user_id);
-- rollback DROP INDEX refresh_tokens_users__fk;

-- changeset David.Rabko:6_MED_210_Login
CREATE INDEX verification_tokens_users__fk ON verification_tokens (user_id);
-- rollback DROP INDEX verification_tokens_users__fk;

-- changeset David.Rabko:7_MED_210_Login
CREATE INDEX users_roles__fk ON users (role);
-- rollback DROP INDEX users_roles__fk;

-- changeset Uladzislau.Lukashevich:29_MED_144_Appointment
CREATE INDEX consultation_appointments_doctors__fk ON consultation_appointments (doctor_id);
-- rollback DROP INDEX consultation_appointments_doctors__fk;

-- changeset Uladzislau.Lukashevich:30_MED_144_Appointment
CREATE INDEX consultation_appointments_patients__fk ON consultation_appointments (patient_id);
-- rollback DROP INDEX consultation_appointments_patients__fk;

-- changeset Uladzislau.Lukashevich:31_MED_144_Appointment
CREATE INDEX consultation_appointments_locations__fk ON consultation_appointments (location_id);
-- rollback DROP INDEX consultation_appointments_locations__fk;

-- changeset Uladzislau.Lukashevich:32_MED_144_Appointment
CREATE INDEX consultation_appointments_appointment_statuses__fk ON consultation_appointments (appointment_status);
-- rollback DROP INDEX consultation_appointments_appointment_statuses__fk;
