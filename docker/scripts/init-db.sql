-- Database initialization script for DocMate Platform
-- This script creates the shared database and enables necessary extensions

-- Create database if it doesn't exist (PostgreSQL doesn't support IF NOT EXISTS for CREATE DATABASE)
-- This will be handled by Docker's POSTGRES_DB environment variable

-- Enable UUID extension for generating UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enable pg_trgm extension for text search capabilities
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Enable btree_gist extension for additional index types
CREATE EXTENSION IF NOT EXISTS "btree_gist";

-- Create schemas for different services (optional - can help organize tables)
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS users;
CREATE SCHEMA IF NOT EXISTS appointments;
CREATE SCHEMA IF NOT EXISTS payments;
CREATE SCHEMA IF NOT EXISTS notifications;
CREATE SCHEMA IF NOT EXISTS files;
CREATE SCHEMA IF NOT EXISTS prescriptions;
CREATE SCHEMA IF NOT EXISTS availability;
CREATE SCHEMA IF NOT EXISTS taxonomy;
CREATE SCHEMA IF NOT EXISTS admin;

-- Grant permissions to the docmate user
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO docmate;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO docmate;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO docmate;

GRANT ALL PRIVILEGES ON SCHEMA auth TO docmate;
GRANT ALL PRIVILEGES ON SCHEMA users TO docmate;
GRANT ALL PRIVILEGES ON SCHEMA appointments TO docmate;
GRANT ALL PRIVILEGES ON SCHEMA payments TO docmate;
GRANT ALL PRIVILEGES ON SCHEMA notifications TO docmate;
GRANT ALL PRIVILEGES ON SCHEMA files TO docmate;
GRANT ALL PRIVILEGES ON SCHEMA prescriptions TO docmate;
GRANT ALL PRIVILEGES ON SCHEMA availability TO docmate;
GRANT ALL PRIVILEGES ON SCHEMA taxonomy TO docmate;
GRANT ALL PRIVILEGES ON SCHEMA admin TO docmate;

-- Set default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO docmate;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO docmate;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO docmate;
