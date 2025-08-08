-- Enable UUID support
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- USERS TABLE (Unified authentication)
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    password_hash TEXT NOT NULL,
    role VARCHAR(20) CHECK (role IN ('PATIENT', 'DOCTOR', 'ADMIN')) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    profile_picture TEXT,
    email_verified BOOLEAN DEFAULT false,
    phone_verified BOOLEAN DEFAULT false,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SPECIALIZATIONS (Master data)
CREATE TABLE IF NOT EXISTS specializations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CONDITIONS (Master data for filtering)
CREATE TABLE IF NOT EXISTS conditions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ADDRESSES
CREATE TABLE IF NOT EXISTS addresses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    address_line1 VARCHAR(200) NOT NULL,
    address_line2 VARCHAR(200),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) DEFAULT 'United States',
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    is_primary BOOLEAN DEFAULT false,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PATIENTS
CREATE TABLE IF NOT EXISTS patients (
    id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    date_of_birth DATE,
    gender VARCHAR(10) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    blood_type VARCHAR(5),
    height VARCHAR(10),
    weight VARCHAR(10),
    address_id UUID REFERENCES addresses(id),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PATIENT MEDICAL HISTORY
CREATE TABLE IF NOT EXISTS patient_medical_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID REFERENCES patients(id) ON DELETE CASCADE,
    allergies TEXT,
    chronic_conditions TEXT,
    current_medications TEXT,
    previous_surgeries TEXT,
    family_history TEXT,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(15),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- DOCTORS
CREATE TABLE IF NOT EXISTS doctors (
    id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    specialization_id UUID REFERENCES specializations(id),
    license_number VARCHAR(50) UNIQUE NOT NULL,
    experience_years INTEGER DEFAULT 0,
    fee_per_consultation NUMERIC(10,2) DEFAULT 0.00,
    bio TEXT,
    video_consultation_enabled BOOLEAN DEFAULT false,
    tele_consultation_enabled BOOLEAN DEFAULT false,
    emergency_available BOOLEAN DEFAULT false,
    is_approved BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    clinic_name VARCHAR(200),
    primary_address_id UUID REFERENCES addresses(id),
    average_rating NUMERIC(3,2) DEFAULT 0.00,
    review_count INTEGER DEFAULT 0,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- DOCTOR SPECIALIZATIONS (Many-to-many)
CREATE TABLE IF NOT EXISTS doctor_specializations (
    doctor_id UUID REFERENCES doctors(id) ON DELETE CASCADE,
    specialization_id UUID REFERENCES specializations(id) ON DELETE CASCADE,
    PRIMARY KEY (doctor_id, specialization_id)
);

-- DOCTOR CONDITIONS (Many-to-many)
CREATE TABLE IF NOT EXISTS doctor_conditions (
    doctor_id UUID REFERENCES doctors(id) ON DELETE CASCADE,
    condition_id UUID REFERENCES conditions(id) ON DELETE CASCADE,
    PRIMARY KEY (doctor_id, condition_id)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_patients_user_id ON patients(id);
CREATE INDEX IF NOT EXISTS idx_doctors_user_id ON doctors(id);
CREATE INDEX IF NOT EXISTS idx_doctors_specialization ON doctors(specialization_id);
CREATE INDEX IF NOT EXISTS idx_doctors_approved ON doctors(is_approved);
CREATE INDEX IF NOT EXISTS idx_doctors_active ON doctors(is_active);

-- Insert sample specializations
INSERT INTO specializations (name, description) VALUES
('Cardiology', 'Heart and cardiovascular system'),
('Dermatology', 'Skin, hair, and nail conditions'),
('Neurology', 'Brain and nervous system disorders'),
('Pediatrics', 'Medical care for infants, children, and adolescents'),
('Orthopedics', 'Musculoskeletal system'),
('Psychiatry', 'Mental health and behavioral disorders'),
('General Medicine', 'Primary care and general health issues'),
('Gynecology', 'Women''s reproductive health'),
('Ophthalmology', 'Eye and vision care'),
('ENT', 'Ear, nose, and throat disorders')
ON CONFLICT (name) DO NOTHING;

-- Insert sample conditions
INSERT INTO conditions (name, description) VALUES
('Diabetes', 'Blood sugar regulation disorders'),
('Hypertension', 'High blood pressure'),
('Asthma', 'Respiratory condition affecting breathing'),
('Arthritis', 'Joint inflammation and pain'),
('Depression', 'Mental health condition affecting mood'),
('Anxiety', 'Mental health condition causing excessive worry'),
('Migraine', 'Severe recurring headaches'),
('Back Pain', 'Pain in the back or spine'),
('Skin Allergies', 'Allergic reactions affecting the skin'),
('Heart Disease', 'Various conditions affecting the heart')
ON CONFLICT (name) DO NOTHING;
