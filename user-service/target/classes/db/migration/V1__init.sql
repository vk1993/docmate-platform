-- Initial schema for user-service. For demonstration we reuse the docmate_users table
-- defined in the auth-service module. In a full implementation, separate
-- patient_profile, doctor_profile and other tables would be created here.
CREATE TABLE IF NOT EXISTS docmate_users (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);