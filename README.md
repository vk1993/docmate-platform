# DocMate Healthcare Platform

A comprehensive microservices-based healthcare appointment booking platform that connects patients with qualified doctors. The platform supports multiple consultation modes (video, telephonic, and offline) with integrated payment processing, prescription management, and administrative controls.

## 🏗️ Architecture Overview

DocMate is built using a modern microservices architecture with **13 services** optimized for scalability, performance, and security.

### 🚀 Core Services
- **Gateway Service** (Port 8080) - API Gateway with Spring Cloud Gateway
- **Auth Service** (Port 8081) - JWT-based authentication and authorization
- **User Service** (Port 8082) - User management for patients and doctors
- **Admin Service** (Port 8083) - Administrative operations and verification
- **Appointment Service** (Port 8084) - Appointment booking and management
- **Availability Service** (Port 8085) - Doctor availability scheduling
- **File Service** (Port 8086) - Document upload/management with S3
- **Notification Service** (Port 8087) - Email and SMS notifications
- **Payment Service** (Port 8088) - Stripe payment processing
- **Prescription Service** (Port 8089) - Digital prescription management
- **Taxonomy Service** (Port 8090) - Medical specializations and conditions
- **DB Migration** - Centralized database schema management

### 🏛️ Infrastructure Components
- **PostgreSQL 15** - Primary database with optimized schemas
- **Redis 7** - Session management and caching
- **LocalStack** - AWS S3 simulation for development
- **Prometheus** - Metrics collection and monitoring
- **Grafana** - Visualization dashboards and alerting

## 🐳 Docker Deployment Options

We provide **4 optimized Docker image types** for different use cases:

### Image Types & Sizes
| Type | Size | Use Case | Security |
|------|------|----------|----------|
| **Distroless** | ~100MB | Production | Ultra-secure (no shell) |
| **Alpine** | ~150MB | Development | Small with shell access |
| **Minimal** | ~80MB | Resource-constrained | Custom JRE |
| **Standard** | ~300MB | Full-featured | Complete JDK |

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- 8GB+ RAM available
- Java 17+ (for local development)
- Maven 3.8+ (for local development)

### 1. Clone and Setup
```bash
git clone <repository-url>
cd docmate-platform
```

### 2. Build Lightweight Docker Images
```bash
# Build all services with distroless images (recommended)
./docker/build-lightweight.sh -t distroless

# Alternative: build with other image types
./docker/build-lightweight.sh -t alpine    # With shell access
./docker/build-lightweight.sh -t minimal   # Smallest size
./docker/build-lightweight.sh -t standard  # Full JDK

# Build specific service only
./docker/build-lightweight.sh -t distroless -s auth-service

# Clean build (no cache)
./docker/build-lightweight.sh -t distroless -c
```

### 3. Start the Platform
```bash
cd docker

# Start with distroless images
IMAGE_TYPE=distroless docker-compose up -d

# Or use environment file
cp .env.example .env
# Edit .env file as needed
docker-compose up -d
```

### 4. Access the Platform
- **API Gateway**: http://localhost:8080
- **Grafana Dashboard**: http://localhost:3000 (admin/admin123)
- **Prometheus Metrics**: http://localhost:9090
- **Database**: localhost:5432 (docmate/password)

## 📊 System Architecture

```
┌─────────────────┐    ┌─────────────────────────────────┐
│   Client Apps   │───▶│         API Gateway             │
│   (Web/Mobile)  │    │         (Port 8080)             │
└─────────────────┘    └─────────────┬───────────────────┘
                                     │
        ┌────────────────────────────┼────────────────────────────┐
        │                            │                            │
   ┌────▼─────┐  ┌─────▼──────┐  ┌──▼────────┐  ┌────▼─────────┐
   │   Auth   │  │    User    │  │   Admin   │  │ Appointment  │
   │ Service  │  │  Service   │  │ Service   │  │   Service    │
   │  :8081   │  │   :8082    │  │  :8083    │  │    :8084     │
   └──────────┘  └────────────┘  └───────────┘  └──────────────┘
        │                            │                            │
   ┌────▼─────┐  ┌─────▼──────�����  ┌──▼────────┐  ┌────▼��─��──────┐
   │Available │  │    File    │  │Notification│  │   Payment    │
   │ Service  │  │  Service   │  │ Service   │  │   Service    │
   │  :8085   │  │   :8086    │  │  :8087    │  │    :8088     │
   └────���─────┘  └────────────┘  └───────────┘  └──────────────┘
        │                            │                            │
   ┌────▼─────┐  ┌─────▼──────┐
   │Prescription│ │ Taxonomy   │
   │ Service  │  │  Service   │
   │  :8089   │  │   :8090    │
   └──────────┘  └────────────┘
        │                            │
        └────────────┬───────────────┘
                     │
   ┌─────────────────▼─────────────────┐
   │         Data Layer                │
   │  PostgreSQL | Redis | LocalStack  │
   │    :5432    | :6379 |   :4566     │
   └───────────────────────────────────┘
```

## 🔧 Development

### Local Development Setup
```bash
# Install dependencies
mvn clean install

# Run individual service
cd auth-service
mvn spring-boot:run

# Run with profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Database Migration
```bash
# Run migrations manually
cd db-migration
mvn spring-boot:run
```

### Testing
```bash
# Run all tests
mvn test

# Run specific service tests
cd auth-service
mvn test

# Run integration tests
mvn verify
```

## 🔒 Security Features

- **JWT Authentication** with refresh tokens
- **Role-based Access Control** (PATIENT, DOCTOR, ADMIN)
- **Method-level Security** with @PreAuthorize
- **HTTPS/TLS** enforced in production
- **Rate Limiting** at gateway level
- **Container Security** with distroless images
- **Database Encryption** for sensitive data

## 📈 Performance Optimizations

### Docker Optimizations
- **Multi-stage builds** for smaller images
- **Layer caching** for faster rebuilds
- **Custom JRE** for minimal runtime
- **Optimized .dockerignore** for faster builds
- **Container-aware JVM** settings

### Application Optimizations
- **Connection pooling** with HikariCP
- **Redis caching** for frequently accessed data
- **Asynchronous processing** for notifications
- **Database indexing** for query performance
- **Pagination** for large data sets

## 🔍 Monitoring & Observability

### Metrics & Monitoring
- **Prometheus** metrics collection
- **Grafana** dashboards and alerting
- **Spring Boot Actuator** health checks
- **Custom business metrics** tracking
- **Performance monitoring** with JVM metrics

### Health Checks
All services expose health check endpoints:
```bash
# Check service health
curl http://localhost:8081/actuator/health

# Check all services through gateway
curl http://localhost:8080/actuator/health
```

## 🌐 API Documentation

### Base URLs
- **Gateway**: `http://localhost:8080` (Production entry point)
- **Direct Service Access**: `http://localhost:808X` (Development only)

All production API calls should go through the Gateway for proper routing, authentication, and rate limiting.

---

## 📋 Complete API Endpoints

### 🔐 Auth Service (Port 8081)

#### Authentication & Registration
```bash
# User Registration
POST /api/auth/register/patient
Content-Type: application/json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123",
  "phone": "+1234567890",
  "dateOfBirth": "1990-01-01",
  "gender": "MALE"
}

POST /api/auth/register/doctor
Content-Type: application/json
{
  "fullName": "Dr. Jane Smith",
  "email": "dr.jane@example.com",
  "password": "securePassword123",
  "phone": "+1234567890",
  "licenseNumber": "MD123456",
  "specializations": ["CARDIOLOGY"],
  "experience": 5
}

# User Authentication
POST /api/auth/login
Content-Type: application/json
{
  "email": "user@example.com",
  "password": "password123"
}

# Token Management
POST /api/auth/refresh
Content-Type: application/json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

POST /api/auth/logout
Authorization: Bearer <token>

# Password Management
POST /api/auth/forgot-password
Content-Type: application/json
{
  "email": "user@example.com"
}

POST /api/auth/reset-password
Content-Type: application/json
{
  "token": "reset_token_here",
  "newPassword": "newSecurePassword123"
}

# User Profile
GET /api/auth/me
Authorization: Bearer <token>
```

---

### 👥 User Service (Port 8082)

#### User Profile Management
```bash
# Get User Profile
GET /api/users/profile
Authorization: Bearer <token>

# Update User Profile
PUT /api/users/profile
Authorization: Bearer <token>
Content-Type: application/json
{
  "fullName": "Updated Name",
  "phone": "+1234567890",
  "profilePicture": "https://example.com/photo.jpg"
}
```

#### Doctor Management
```bash
# Search Doctors
GET /api/doctors/search?query=cardiology&specializationId=uuid&conditionId=uuid&page=0&size=10
GET /api/doctors/search?location=New York&available=true&consultationMode=VIDEO

# Get Doctor Details
GET /api/doctors/{doctorId}

# Update Doctor Profile (Doctor only)
PUT /api/doctors/profile
Authorization: Bearer <token>
Content-Type: application/json
{
  "bio": "Experienced cardiologist...",
  "consultationFee": 150.00,
  "consultationModes": ["VIDEO", "PHONE", "OFFLINE"],
  "languages": ["English", "Spanish"],
  "awards": ["Best Doctor 2023"]
}

# Get Doctor Statistics (Doctor only)
GET /api/doctors/stats
Authorization: Bearer <token>

# Update Doctor Availability (Doctor only)
PUT /api/doctors/availability
Authorization: Bearer <token>
Content-Type: application/json
{
  "recurringSlots": [
    {
      "dayOfWeek": "MONDAY",
      "startTime": "09:00",
      "endTime": "17:00",
      "slotDuration": 30
    }
  ]
}
```

#### Patient Management
```bash
# Get Patient Profile (Patient only)
GET /api/patients/profile
Authorization: Bearer <token>

# Update Patient Profile (Patient only)
PUT /api/patients/profile
Authorization: Bearer <token>
Content-Type: application/json
{
  "emergencyContact": "+1234567890",
  "bloodGroup": "O+",
  "allergies": ["Penicillin"],
  "chronicConditions": ["Diabetes"]
}

# Medical History
PUT /api/patients/medical-history
Authorization: Bearer <token>
Content-Type: application/json
{
  "allergies": ["Penicillin", "Shellfish"],
  "chronicConditions": ["Hypertension"],
  "medications": ["Lisinopril 10mg"],
  "surgeries": ["Appendectomy (2020)"],
  "familyHistory": "Heart disease"
}

# Patient Reports
GET /api/patients/reports?page=0&size=10
Authorization: Bearer <token>

POST /api/patients/reports
Authorization: Bearer <token>
Content-Type: multipart/form-data
file: <medical_report.pdf>
title: "Blood Test Results"
reportType: "LAB_REPORT"

DELETE /api/patients/reports/{reportId}
Authorization: Bearer <token>
```

---

### 🏥 Admin Service (Port 8083)

#### Dashboard & Analytics
```bash
# Admin Dashboard Statistics
GET /api/admin/dashboard
Authorization: Bearer <token> (Admin only)

# System Health
GET /api/admin/health
Authorization: Bearer <token> (Admin only)
```

#### Doctor Verification Management
```bash
# Get Pending Verifications
GET /api/admin/verifications/pending
Authorization: Bearer <token> (Admin only)

# Get Verification Details
GET /api/admin/verifications/{verificationId}
Authorization: Bearer <token> (Admin only)

# Review Verification
POST /api/admin/verifications/review
Authorization: Bearer <token> (Admin only)
Content-Type: application/json
{
  "verificationId": "uuid",
  "status": "APPROVED",
  "comments": "All documents verified successfully"
}

# Get Verifications by Status
GET /api/admin/verifications?status=PENDING&page=0&size=10
Authorization: Bearer <token> (Admin only)

# Get Pending Count
GET /api/admin/verifications/count
Authorization: Bearer <token> (Admin only)
```

#### User Management
```bash
# Get All Users
GET /api/admin/users?role=DOCTOR&status=ACTIVE&page=0&size=20
Authorization: Bearer <token> (Admin only)

# Suspend/Activate User
PUT /api/admin/users/{userId}/status
Authorization: Bearer <token> (Admin only)
Content-Type: application/json
{
  "status": "SUSPENDED",
  "reason": "Policy violation"
}

# Get User Details
GET /api/admin/users/{userId}
Authorization: Bearer <token> (Admin only)
```

---

### 📅 Appointment Service (Port 8084)

#### Appointment Management
```bash
# Create Appointment
POST /api/appointments
Authorization: Bearer <token>
Content-Type: application/json
{
  "doctorId": "uuid",
  "appointmentDateTime": "2025-08-15T10:00:00",
  "consultationMode": "VIDEO",
  "symptoms": "Chest pain and shortness of breath",
  "notes": "First time consultation"
}

# Get User Appointments
GET /api/appointments?status=CONFIRMED&page=0&size=10
Authorization: Bearer <token>

# Get Appointment Details
GET /api/appointments/{appointmentId}
Authorization: Bearer <token>

# Update Appointment
PUT /api/appointments/{appointmentId}
Authorization: Bearer <token>
Content-Type: application/json
{
  "appointmentDateTime": "2025-08-15T11:00:00",
  "notes": "Updated timing due to emergency"
}

# Cancel Appointment
PUT /api/appointments/{appointmentId}/cancel
Authorization: Bearer <token>
Content-Type: application/json
{
  "cancellationReason": "Personal emergency",
  "notifyDoctor": true
}

# Confirm Appointment (Doctor only)
PUT /api/appointments/{appointmentId}/confirm
Authorization: Bearer <token>

# Complete Appointment (Doctor only)
PUT /api/appointments/{appointmentId}/complete
Authorization: Bearer <token>
Content-Type: application/json
{
  "consultationNotes": "Patient responded well to treatment",
  "prescriptionRequired": true
}

# Get Upcoming Appointments
GET /api/appointments/upcoming
Authorization: Bearer <token>

# Get Appointment History
GET /api/appointments/history?page=0&size=10
Authorization: Bearer <token>

# Reschedule Appointment
PUT /api/appointments/{appointmentId}/reschedule
Authorization: Bearer <token>
Content-Type: application/json
{
  "newDateTime": "2025-08-16T10:00:00",
  "reason": "Doctor availability changed"
}
```

---

### ⏰ Availability Service (Port 8085)

#### Doctor Availability Management
```bash
# Set Recurring Availability (Doctor only)
POST /api/availability/recurring
Authorization: Bearer <token>
Content-Type: application/json
{
  "dayOfWeek": "MONDAY",
  "startTime": "09:00",
  "endTime": "17:00",
  "slotDuration": 30,
  "breakStartTime": "12:00",
  "breakEndTime": "13:00"
}

# Set Ad-hoc Availability (Doctor only)
POST /api/availability/adhoc
Authorization: Bearer <token>
Content-Type: application/json
{
  "date": "2025-08-15",
  "timeSlots": [
    {
      "startTime": "10:00",
      "endTime": "10:30",
      "consultationMode": "VIDEO"
    }
  ]
}

# Get Doctor Availability
GET /api/availability/{doctorId}?startDate=2025-08-15&endDate=2025-08-22
GET /api/availability/{doctorId}/slots?date=2025-08-15&mode=VIDEO

# Update Availability (Doctor only)
PUT /api/availability/{availabilityId}
Authorization: Bearer <token>
Content-Type: application/json
{
  "startTime": "10:00",
  "endTime": "18:00",
  "isAvailable": true
}

# Delete Availability (Doctor only)
DELETE /api/availability/{availabilityId}
Authorization: Bearer <token>

# Get Available Time Slots
GET /api/availability/{doctorId}/slots?date=2025-08-15&mode=VIDEO

# Block Time Slot (Doctor only)
POST /api/availability/block
Authorization: Bearer <token>
Content-Type: application/json
{
  "startDateTime": "2025-08-15T14:00:00",
  "endDateTime": "2025-08-15T15:00:00",
  "reason": "Personal appointment"
}
```

---

### 📁 File Service (Port 8086)

#### File Management
```bash
# Upload File
POST /api/files/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data
file: <file_to_upload>
category: "MEDICAL_REPORT"
description: "Blood test results"

# Download File
GET /api/files/{fileId}/download
Authorization: Bearer <token>

# Get File Metadata
GET /api/files/{fileId}
Authorization: Bearer <token>

# Delete File
DELETE /api/files/{fileId}
Authorization: Bearer <token>

# Get User Files
GET /api/files/user?category=MEDICAL_REPORT&page=0&size=10
Authorization: Bearer <token>

# Share File
POST /api/files/{fileId}/share
Authorization: Bearer <token>
Content-Type: application/json
{
  "shareWithUserId": "uuid",
  "permissions": ["READ"],
  "expiryDate": "2025-08-30"
}

# Get Shared Files
GET /api/files/shared?page=0&size=10
Authorization: Bearer <token>
```

---

### 🔔 Notification Service (Port 8087)

#### Notification Management
```bash
# Send Notification (Internal/Admin)
POST /api/notifications/send
Authorization: Bearer <token>
Content-Type: application/json
{
  "recipientId": "uuid",
  "type": "APPOINTMENT_REMINDER",
  "title": "Appointment Reminder",
  "message": "Your appointment is in 1 hour",
  "channels": ["EMAIL", "SMS"]
}

# Get User Notifications
GET /api/notifications?read=false&page=0&size=20
Authorization: Bearer <token>

# Mark Notification as Read
PUT /api/notifications/{notificationId}/read
Authorization: Bearer <token>

# Delete Notification
DELETE /api/notifications/{notificationId}
Authorization: Bearer <token>

# Get Unread Count
GET /api/notifications/unread-count
Authorization: Bearer <token>

# Mark All as Read
PUT /api/notifications/mark-all-read
Authorization: Bearer <token>

# Update Notification Preferences
PUT /api/notifications/preferences
Authorization: Bearer <token>
Content-Type: application/json
{
  "emailNotifications": true,
  "smsNotifications": false,
  "appointmentReminders": true,
  "prescriptionAlerts": true
}

# Get Notification Preferences
GET /api/notifications/preferences
Authorization: Bearer <token>
```

---

### 💳 Payment Service (Port 8088)

#### Payment Processing
```bash
# Create Payment Intent
POST /api/payments/create-intent
Authorization: Bearer <token>
Content-Type: application/json
{
  "appointmentId": "uuid",
  "amount": 150.00,
  "currency": "USD",
  "paymentMethod": "card"
}

# Confirm Payment
POST /api/payments/confirm
Authorization: Bearer <token>
Content-Type: application/json
{
  "paymentIntentId": "pi_stripe_payment_intent_id",
  "appointmentId": "uuid"
}

# Get Payment Details
GET /api/payments/{paymentId}
Authorization: Bearer <token>

# Get Payment History
GET /api/payments/history?page=0&size=10
Authorization: Bearer <token>

# Request Refund
POST /api/payments/{paymentId}/refund
Authorization: Bearer <token>
Content-Type: application/json
{
  "reason": "Appointment cancelled by doctor",
  "amount": 150.00
}

# Get Payment Status
GET /api/payments/status/{paymentIntentId}
Authorization: Bearer <token>

# Get Payment Methods (Saved cards)
GET /api/payments/methods
Authorization: Bearer <token>

# Save Payment Method
POST /api/payments/methods
Authorization: Bearer <token>
Content-Type: application/json
{
  "stripePaymentMethodId": "pm_stripe_payment_method_id",
  "isDefault": true
}

# Delete Payment Method
DELETE /api/payments/methods/{paymentMethodId}
Authorization: Bearer <token>

# Get Invoice
GET /api/payments/{paymentId}/invoice
Authorization: Bearer <token>
```

---

### 💊 Prescription Service (Port 8089)

#### Prescription Management
```bash
# Create Prescription (Doctor only)
POST /api/prescriptions
Authorization: Bearer <token>
Content-Type: application/json
{
  "appointmentId": "uuid",
  "patientId": "uuid",
  "diagnosis": "Hypertension",
  "symptoms": "High blood pressure, headaches",
  "medicines": [
    {
      "name": "Lisinopril",
      "dosage": "10mg",
      "frequency": "Once daily",
      "duration": "30 days",
      "instructions": "Take with food"
    }
  ],
  "advice": "Monitor blood pressure daily",
  "followUpDate": "2025-09-15"
}

# Get Prescription Details
GET /api/prescriptions/{prescriptionId}
Authorization: Bearer <token>

# Get Patient Prescriptions
GET /api/prescriptions/patient/{patientId}?page=0&size=10
Authorization: Bearer <token>

# Get Doctor's Prescriptions (Doctor only)
GET /api/prescriptions/doctor?page=0&size=10
Authorization: Bearer <token>

# Update Prescription (Doctor only)
PUT /api/prescriptions/{prescriptionId}
Authorization: Bearer <token>
Content-Type: application/json
{
  "medicines": [
    {
      "name": "Lisinopril",
      "dosage": "15mg",
      "frequency": "Once daily",
      "duration": "30 days",
      "instructions": "Increased dosage - take with food"
    }
  ],
  "advice": "Continue monitoring blood pressure"
}

# Download Prescription PDF
GET /api/prescriptions/{prescriptionId}/download
Authorization: Bearer <token>

# Share Prescription
POST /api/prescriptions/{prescriptionId}/share
Authorization: Bearer <token>
Content-Type: application/json
{
  "shareWithEmail": "pharmacy@example.com",
  "shareType": "PHARMACY"
}

# Get Prescription History
GET /api/prescriptions/history?patientId=uuid&page=0&size=10
Authorization: Bearer <token>

# Mark Prescription as Filled
PUT /api/prescriptions/{prescriptionId}/filled
Authorization: Bearer <token>
Content-Type: application/json
{
  "pharmacyName": "City Pharmacy",
  "filledDate": "2025-08-15"
}
```

---

### 🏷️ Taxonomy Service (Port 8090)

#### Medical Data Management
```bash
# Get All Specializations
GET /api/specializations
GET /api/specializations?active=true

# Get Specialization Details
GET /api/specializations/{specializationId}

# Create Specialization (Admin only)
POST /api/specializations
Authorization: Bearer <token> (Admin only)
Content-Type: application/json
{
  "name": "Cardiology",
  "description": "Heart and cardiovascular system",
  "isActive": true
}

# Update Specialization (Admin only)
PUT /api/specializations/{specializationId}
Authorization: Bearer <token> (Admin only)
Content-Type: application/json
{
  "description": "Updated description",
  "isActive": true
}

# Get All Conditions
GET /api/conditions
GET /api/conditions?specialization=CARDIOLOGY

# Get Condition Details
GET /api/conditions/{conditionId}

# Create Condition (Admin only)
POST /api/conditions
Authorization: Bearer <token> (Admin only)
Content-Type: application/json
{
  "name": "Hypertension",
  "description": "High blood pressure",
  "severity": "MODERATE",
  "specializations": ["CARDIOLOGY"]
}

# Update Condition (Admin only)
PUT /api/conditions/{conditionId}
Authorization: Bearer <token> (Admin only)
Content-Type: application/json
{
  "description": "Updated description",
  "severity": "HIGH"
}

# Search Medical Terms
GET /api/taxonomy/search?query=heart&type=condition
GET /api/taxonomy/search?query=cardio&type=specialization

# Get Popular Specializations
GET /api/specializations/popular?limit=10

# Get Conditions by Specialization
GET /api/specializations/{specializationId}/conditions
```

---

### 🚪 Gateway Service (Port 8080)

#### Gateway Management & Routing
```bash
# Health Check
GET /actuator/health

# Service Discovery
GET /actuator/gateway/routes

# Metrics
GET /actuator/metrics

# Route to Auth Service
POST /api/auth/**
GET /api/auth/**

# Route to User Service
GET /api/users/**
PUT /api/users/**
GET /api/doctors/**
PUT /api/doctors/**
GET /api/patients/**
PUT /api/patients/**

# Route to Appointment Service
GET /api/appointments/**
POST /api/appointments/**
PUT /api/appointments/**

# Route to Other Services
GET /api/admin/**      → Admin Service
GET /api/availability/** → Availability Service
GET /api/files/**      → File Service
GET /api/notifications/** → Notification Service
GET /api/payments/**   → Payment Service
GET /api/prescriptions/** → Prescription Service
GET /api/specializations/** → Taxonomy Service
GET /api/conditions/** → Taxonomy Service
```

---

### 🗄️ DB Migration Service

#### Database Management
```bash
# Health Check
GET /actuator/health

# Migration Status
GET /actuator/liquibase

# Force Migration (Admin only)
POST /actuator/liquibase/update
Authorization: Bearer <token> (Admin only)
```

---

## 📊 Response Formats

### Standard API Response
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { /* response data */ },
  "timestamp": "2025-08-09T10:30:00Z"
}
```

### Paginated Response
```json
{
  "success": true,
  "data": {
    "content": [ /* array of items */ ],
    "totalElements": 100,
    "totalPages": 10,
    "size": 10,
    "number": 0,
    "first": true,
    "last": false
  }
}
```

### Error Response
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "field": "email",
      "message": "Email format is invalid"
    }
  ],
  "timestamp": "2025-08-09T10:30:00Z"
}
```

---

## 🔑 Authentication

### JWT Token Structure
```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyX2lkIiwicm9sZSI6IlBBVElFTlQiLCJleHAiOjE2MjM5MjM0MDB9.signature
```

### Required Headers
```bash
Content-Type: application/json
Authorization: Bearer <token>  # For protected endpoints
X-Request-ID: unique-request-id  # Optional for tracking
```

---

## 📱 Client Integration Examples

### Authentication Flow
```javascript
// Login
const loginResponse = await fetch('/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'user@example.com',
    password: 'password123'
  })
});

const { accessToken, refreshToken } = await loginResponse.json();

// Use token for authenticated requests
const profileResponse = await fetch('/api/users/profile', {
  headers: { 'Authorization': `Bearer ${accessToken}` }
});
```

### Appointment Booking Flow
```javascript
// 1. Search doctors
const doctorsResponse = await fetch('/api/doctors/search?query=cardiology');
const doctors = await doctorsResponse.json();

// 2. Get doctor availability
const availabilityResponse = await fetch(`/api/availability/${doctorId}/slots?date=2025-08-15`);
const slots = await availabilityResponse.json();

// 3. Book appointment
const appointmentResponse = await fetch('/api/appointments', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
  body: JSON.stringify({
    doctorId: doctorId,
    appointmentDateTime: '2025-08-15T10:00:00',
    consultationMode: 'VIDEO',
    symptoms: 'Chest pain'
  })
});
```

### API Documentation Access
- **Swagger UI**: http://localhost:8080/swagger-ui.html (when running)
- **OpenAPI Spec**: Available in each service at `/v3/api-docs`
