-- Enable UUID support
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- SPECIALIZATIONS (Master data)
CREATE TABLE IF NOT EXISTS specializations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CONDITIONS (Master data for filtering)
CREATE TABLE IF NOT EXISTS conditions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_specializations_name ON specializations(name);
CREATE INDEX IF NOT EXISTS idx_specializations_active ON specializations(is_active);
CREATE INDEX IF NOT EXISTS idx_conditions_name ON conditions(name);
CREATE INDEX IF NOT EXISTS idx_conditions_active ON conditions(is_active);

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
