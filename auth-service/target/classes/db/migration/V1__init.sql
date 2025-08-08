-- Initial schema for auth-service. In a full implementation this would
-- include tables such as docmate_users, patient_profile, doctor_profile,
-- and others described in the specification. For this skeleton we
-- define only the docmate_users table to allow JPA to function.

CREATE TABLE IF NOT EXISTS docmate_users (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    license_number VARCHAR(100),
    experience INTEGER,
    bio TEXT,
    fee INTEGER,
    video_consultation_enabled BOOLEAN,
    tele_consultation_enabled BOOLEAN,
    emergency_available BOOLEAN,
    approved BOOLEAN
);