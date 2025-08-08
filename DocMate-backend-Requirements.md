# DocMate Healthcare Platform - Complete Backend Requirements

## Table of Contents
1. [Project Overview](#project-overview)
2. [Updated Database Schema](#updated-database-schema)
3. [API Endpoints Required](#api-endpoints-required)
4. [Data Models](#data-models)
5. [Business Rules & Validation](#business-rules--validation)
6. [Security Requirements](#security-requirements)
7. [Third-party Integrations](#third-party-integrations)
8. [Performance & Scalability](#performance--scalability)

## Project Overview

DocMate is a comprehensive healthcare appointment booking platform that connects patients with qualified doctors. The platform supports multiple consultation modes (video, telephonic, and offline) with integrated payment processing, prescription management, and administrative controls.

### Technology Stack Requirements
- **Backend Framework**: Spring Boot 3.x with Java 17+
- **Database**: PostgreSQL 14+
- **Authentication**: JWT with refresh tokens using Spring Security
- **File Storage**: AWS S3 or similar cloud storage
- **Real-time Communication**: WebSockets with Spring WebSocket
- **Payment Processing**: Stripe or PayPal integration
- **Email/SMS**: SendGrid/Twilio integration
- **Build Tool**: Maven or Gradle
- **Documentation**: OpenAPI 3.0 (Swagger)

## Updated Database Schema

### Core Tables

```sql
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
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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

-- DOCTOR RECURRING AVAILABILITY
CREATE TABLE IF NOT EXISTS doctor_recurring_availability (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    doctor_id UUID REFERENCES doctors(id) ON DELETE CASCADE,
    day_of_week VARCHAR(10) CHECK (day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    capacity INTEGER DEFAULT 1,
    is_active BOOLEAN DEFAULT true,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- DOCTOR AD-HOC AVAILABILITY
CREATE TABLE IF NOT EXISTS doctor_adhoc_availability (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    doctor_id UUID REFERENCES doctors(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    capacity INTEGER DEFAULT 1,
    is_active BOOLEAN DEFAULT true,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- APPOINTMENTS
CREATE TABLE IF NOT EXISTS appointments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    doctor_id UUID REFERENCES doctors(id),
    patient_id UUID REFERENCES patients(id),
    appointment_date_time TIMESTAMP NOT NULL,
    mode VARCHAR(20) CHECK (mode IN ('VIDEO', 'TELE', 'OFFLINE')) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED')) DEFAULT 'PENDING',
    payment_status VARCHAR(20) CHECK (payment_status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED')),
    notes TEXT,
    cancellation_reason TEXT,
    meeting_link TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PRESCRIPTIONS
CREATE TABLE IF NOT EXISTS prescriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    appointment_id UUID REFERENCES appointments(id) ON DELETE CASCADE,
    doctor_id UUID REFERENCES doctors(id),
    patient_id UUID REFERENCES patients(id),
    diagnosis TEXT NOT NULL,
    symptoms TEXT,
    advice TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PRESCRIPTION MEDICINES
CREATE TABLE IF NOT EXISTS prescription_medicines (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    prescription_id UUID REFERENCES prescriptions(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration VARCHAR(100) NOT NULL,
    instructions TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PATIENT REPORTS
CREATE TABLE IF NOT EXISTS patient_reports (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID REFERENCES patients(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    test_date DATE,
    file_url TEXT,
    file_name VARCHAR(500),
    file_type VARCHAR(100),
    file_size BIGINT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PAYMENTS
CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    appointment_id UUID REFERENCES appointments(id),
    amount NUMERIC(10,2) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')) DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    transaction_id VARCHAR(200),
    stripe_payment_intent_id VARCHAR(200),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- DOCTOR VERIFICATION DOCUMENTS
CREATE TABLE IF NOT EXISTS doctor_verification_documents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    doctor_id UUID REFERENCES doctors(id) ON DELETE CASCADE,
    license_document_url TEXT,
    id_document_url TEXT,
    verification_status VARCHAR(20) CHECK (verification_status IN ('PENDING', 'APPROVED', 'REJECTED')) DEFAULT 'PENDING',
    verified_by UUID REFERENCES users(id),
    verified_date TIMESTAMP,
    rejection_reason TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- REVIEWS
CREATE TABLE IF NOT EXISTS reviews (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    appointment_id UUID REFERENCES appointments(id),
    doctor_id UUID REFERENCES doctors(id),
    patient_id UUID REFERENCES patients(id),
    rating INTEGER CHECK (rating >= 1 AND rating <= 5) NOT NULL,
    comment TEXT,
    is_flagged BOOLEAN DEFAULT false,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ADMINS
CREATE TABLE IF NOT EXISTS admins (
    id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    permissions JSONB,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- NOTIFICATIONS
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN DEFAULT false,
    data JSONB,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- AUDIT LOG
CREATE TABLE IF NOT EXISTS audit_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(100),
    old_values JSONB,
    new_values JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



Database Indexes for Performance

-- Authentication and user lookups
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(is_active);

-- Doctor search and filtering
CREATE INDEX idx_doctors_approved ON doctors(is_approved);
CREATE INDEX idx_doctors_active ON doctors(is_active);
CREATE INDEX idx_doctors_specialization ON doctors(specialization_id);
CREATE INDEX idx_doctors_rating ON doctors(average_rating DESC);
CREATE INDEX idx_doctors_fee ON doctors(fee_per_consultation);

-- Appointment queries
CREATE INDEX idx_appointments_doctor_date ON appointments(doctor_id, appointment_date_time);
CREATE INDEX idx_appointments_patient_date ON appointments(patient_id, appointment_date_time);
CREATE INDEX idx_appointments_status ON appointments(status);
CREATE INDEX idx_appointments_date ON appointments(appointment_date_time);

-- Prescription lookups
CREATE INDEX idx_prescriptions_doctor ON prescriptions(doctor_id, created_date DESC);
CREATE INDEX idx_prescriptions_patient ON prescriptions(patient_id, created_date DESC);
CREATE INDEX idx_prescriptions_appointment ON prescriptions(appointment_id);

-- Payment tracking
CREATE INDEX idx_payments_appointment ON payments(appointment_id);
CREATE INDEX idx_payments_status ON payments(status);

-- Availability queries
CREATE INDEX idx_recurring_availability_doctor ON doctor_recurring_availability(doctor_id, day_of_week);
CREATE INDEX idx_adhoc_availability_doctor_date ON doctor_adhoc_availability(doctor_id, date);

-- Full-text search indexes
CREATE INDEX idx_doctors_search ON doctors USING gin(to_tsvector('english', coalesce(bio, '')));
CREATE INDEX idx_specializations_search ON specializations USING gin(to_tsvector('english', name || ' ' || coalesce(description, '')));


Trigger Functions

-- Update timestamp trigger
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_date = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply triggers to tables with updated_date
CREATE TRIGGER update_users_modtime BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER update_patients_modtime BEFORE UPDATE ON patients FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER update_doctors_modtime BEFORE UPDATE ON doctors FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER update_appointments_modtime BEFORE UPDATE ON appointments FOR EACH ROW EXECUTE FUNCTION update_modified_column();


API Endpoints Required
Spring Boot Controller Structure
Authentication Controller

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request);
    
    @PostMapping("/register/patient")
    public ResponseEntity<AuthResponse> registerPatient(@RequestBody PatientRegistrationRequest request);
    
    @PostMapping("/register/doctor")
    public ResponseEntity<AuthResponse> registerDoctor(@RequestBody DoctorRegistrationRequest request);
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token);
    
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser();
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request);
    
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request);
    
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request);
}

Taxonomy Controller

@RestController
@RequestMapping("/api/taxonomy")
public class TaxonomyController {
    
    @GetMapping("/specializations")
    public ResponseEntity<List<SpecializationResponse>> getSpecializations();
    
    @GetMapping("/conditions")
    public ResponseEntity<List<ConditionResponse>> getConditions();
}

@RestController
@RequestMapping("/api/admin/taxonomy")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTaxonomyController {
    
    @PostMapping("/specializations")
    public ResponseEntity<SpecializationResponse> createSpecialization(@RequestBody CreateSpecializationRequest request);
    
    @PutMapping("/specializations/{id}")
    public ResponseEntity<SpecializationResponse> updateSpecialization(@PathVariable UUID id, @RequestBody UpdateSpecializationRequest request);
    
    @DeleteMapping("/specializations/{id}")
    public ResponseEntity<Void> deleteSpecialization(@PathVariable UUID id);
}


Doctor Controller

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<DoctorSearchResponse>> searchDoctors(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) UUID specializationId,
        @RequestParam(required = false) UUID conditionId,
        @RequestParam(required = false) BigDecimal maxFee,
        @RequestParam(required = false) String consultationType,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    );
    
    @GetMapping("/me/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorProfileResponse> getDoctorProfile();
    
    @PutMapping("/me/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorProfileResponse> updateDoctorProfile(@RequestBody UpdateDoctorProfileRequest request);
    
    @GetMapping("/me/stats")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorStatsResponse> getDoctorStats();
    
    @GetMapping("/me/availability/recurring")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<RecurringAvailabilityResponse>> getRecurringAvailability();
    
    @PostMapping("/me/availability/recurring")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<RecurringAvailabilityResponse> createRecurringAvailability(@RequestBody CreateRecurringAvailabilityRequest request);
    
    @DeleteMapping("/me/availability/recurring/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> deleteRecurringAvailability(@PathVariable UUID id);
    
    @GetMapping("/me/availability/adhoc")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<AdhocAvailabilityResponse>> getAdhocAvailability();
    
    @PostMapping("/me/availability/adhoc")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AdhocAvailabilityResponse> createAdhocAvailability(@RequestBody CreateAdhocAvailabilityRequest request);
}


Patient Controller
@RestController
@RequestMapping("/api/patients")
@PreAuthorize("hasRole('PATIENT')")
public class PatientController {
    
    @GetMapping("/me/profile")
    public ResponseEntity<PatientProfileResponse> getPatientProfile();
    
    @PutMapping("/me/profile")
    public ResponseEntity<PatientProfileResponse> updatePatientProfile(@RequestBody UpdatePatientProfileRequest request);
    
    @PutMapping("/me/medical-history")
    public ResponseEntity<PatientMedicalHistoryResponse> updateMedicalHistory(@RequestBody UpdatePatientMedicalHistoryRequest request);
    
    @GetMapping("/me/reports")
    public ResponseEntity<PagedResponse<PatientReportResponse>> getPatientReports(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    );
    
    @PostMapping("/me/reports")
    public ResponseEntity<PatientReportResponse> uploadPatientReport(@RequestParam("file") MultipartFile file,
                                                                    @RequestParam("title") String title,
                                                                    @RequestParam(value = "description", required = false) String description,
                                                                    @RequestParam(value = "testDate", required = false) LocalDate testDate);
    
    @DeleteMapping("/me/reports/{id}")
    public ResponseEntity<Void> deletePatientReport(@PathVariable UUID id);
}


Appointment Controller


@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    
    @GetMapping("/doctor/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PagedResponse<AppointmentResponse>> getDoctorAppointments(
        @RequestParam(required = false) String status,
        @RequestParam(required = false) LocalDate date,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    );
    
    @GetMapping("/patient/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PagedResponse<AppointmentResponse>> getPatientAppointments(
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    );
    
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponse> bookAppointment(@RequestBody BookAppointmentRequest request);
    
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AppointmentResponse> confirmAppointment(@PathVariable UUID id);
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable UUID id, @RequestBody CancelAppointmentRequest request);
    
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<AppointmentResponse> rescheduleAppointment(@PathVariable UUID id, @RequestBody RescheduleAppointmentRequest request);
}


Prescription Controller
@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {
    
    @GetMapping("/doctor/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PagedResponse<PrescriptionResponse>> getDoctorPrescriptions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    );
    
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PrescriptionResponse> createPrescription(@RequestBody CreatePrescriptionRequest request);
    
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPrescriptionPdf(@PathVariable UUID id);
}

Payment Controller
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    @PostMapping("/checkout")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PaymentCheckoutResponse> createPaymentCheckout(@RequestBody PaymentCheckoutRequest request);
    
    @PostMapping("/simulate-success")
    public ResponseEntity<PaymentResponse> simulatePaymentSuccess(@RequestBody PaymentSimulationRequest request);
    
    @PostMapping("/stripe/webhook")
    public ResponseEntity<Void> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature);
}


Admin Controller

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @GetMapping("/doctors/pending")
    public ResponseEntity<PagedResponse<DoctorVerificationResponse>> getPendingDoctors(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    );
    
    @PutMapping("/doctors/{id}/approve")
    public ResponseEntity<DoctorVerificationResponse> approveDoctor(@PathVariable UUID id);
    
    @PutMapping("/doctors/{id}/reject")
    public ResponseEntity<DoctorVerificationResponse> rejectDoctor(@PathVariable UUID id, @RequestBody RejectDoctorRequest request);
    
    @GetMapping("/doctors/{id}/documents/{type}")
    public ResponseEntity<byte[]> downloadDoctorDocument(@PathVariable UUID id, @PathVariable String type);
}


Data Models
Spring Boot Entity Classes

User Entity

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(length = 15)
    private String phone;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "profile_picture")
    private String profilePicture;
    
    @Column(name = "email_verified")
    private Boolean emailVerified = false;
    
    @Column(name = "phone_verified")
    private Boolean phoneVerified = false;
    
    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}

public enum UserRole {
    PATIENT, DOCTOR, ADMIN
}

Doctor Entity

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id
    private UUID id;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;
    
    @Column(name = "license_number", unique = true, nullable = false, length = 50)
    private String licenseNumber;
    
    @Column(name = "experience_years")
    private Integer experienceYears = 0;
    
    @Column(name = "fee_per_consultation", precision = 10, scale = 2)
    private BigDecimal feePerConsultation = BigDecimal.ZERO;
    
    private String bio;
    
    @Column(name = "video_consultation_enabled")
    private Boolean videoConsultationEnabled = false;
    
    @Column(name = "tele_consultation_enabled")
    private Boolean teleConsultationEnabled = false;
    
    @Column(name = "emergency_available")
    private Boolean emergencyAvailable = false;
    
    @Column(name = "is_approved")
    private Boolean isApproved = false;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "clinic_name", length = 200)
    private String clinicName;
    
    @ManyToOne
    @JoinColumn(name = "primary_address_id")
    private Address primaryAddress;
    
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;
    
    @Column(name = "review_count")
    private Integer reviewCount = 0;
    
    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    @ManyToMany
    @JoinTable(
        name = "doctor_specializations",
        joinColumns = @JoinColumn(name = "doctor_id"),
        inverseJoinColumns = @JoinColumn(name = "specialization_id")
    )
    private Set<Specialization> specializations = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "doctor_conditions",
        joinColumns = @JoinColumn(name = "doctor_id"),
        inverseJoinColumns = @JoinColumn(name = "condition_id")
    )
    private Set<Condition> conditions = new HashSet<>();
}

Appointment Entity

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @Column(name = "appointment_date_time", nullable = false)
    private LocalDateTime appointmentDateTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsultationMode mode;
    
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;
    
    private String notes;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    @Column(name = "meeting_link")
    private String meetingLink;
    
    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}

public enum ConsultationMode {
    VIDEO, TELE, OFFLINE
}

public enum AppointmentStatus {
    PENDING, CONFIRMED, CANCELLED, COMPLETED
}

public enum PaymentStatus {
    PENDING, PAID, FAILED, REFUNDED
}

DTO Classes
Request DTOs

@Data
@NotNull
public class LoginRequest {
    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    @Size(min = 6)
    private String password;
}

@Data
@NotNull
public class BookAppointmentRequest {
    @NotNull
    private UUID doctorId;
    
    @NotNull
    @Future
    private LocalDateTime appointmentDateTime;
    
    @NotNull
    private ConsultationMode mode;
    
    private String notes;
}

@Data
@NotNull
public class CreatePrescriptionRequest {
    @NotNull
    private UUID appointmentId;
    
    @NotBlank
    private String diagnosis;
    
    private String symptoms;
    private String advice;
    
    @Valid
    @NotEmpty
    private List<PrescriptionMedicineRequest> medicines;
}

@Data
@NotNull
public class PrescriptionMedicineRequest {
    @NotBlank
    private String name;
    
    @NotBlank
    private String dosage;
    
    @NotBlank
    private String frequency;
    
    @NotBlank
    private String duration;
    
    private String instructions;
}

Response DTOs
@Data
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UserProfileResponse user;
    private String tokenType = "Bearer";
    private Long expiresIn;
}

@Data
public class UserProfileResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
    private String profilePicture;
    private Boolean emailVerified;
    private Boolean phoneVerified;
}

@Data
public class DoctorSearchResponse {
    private UUID id;
    private String fullName;
    private String profilePicture;
    private SpecializationResponse specialization;
    private Integer experienceYears;
    private BigDecimal feePerConsultation;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private String clinicName;
    private AddressResponse primaryAddress;
    private Boolean videoConsultationEnabled;
    private Boolean teleConsultationEnabled;
    private Boolean emergencyAvailable;
    private List<ConditionResponse> conditions;
}

@Data
public class AppointmentResponse {
    private UUID id;
    private DoctorBasicResponse doctor;
    private PatientBasicResponse patient;
    private LocalDateTime appointmentDateTime;
    private ConsultationMode mode;
    private AppointmentStatus status;
    private PaymentStatus paymentStatus;
    private String notes;
    private String meetingLink;
    private LocalDateTime createdDate;
}

@Data
public class PagedResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
}

Business Rules & Validation

Appointment Booking Rules

Spring Boot Validation

@Component
public class AppointmentValidator {
    
    public void validateAppointmentBooking(BookAppointmentRequest request, Doctor doctor) {
        // Rule 1: Appointment must be at least 2 hours in advance
        if (request.getAppointmentDateTime().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Appointments must be booked at least 2 hours in advance");
        }
        
        //Rule 2: Appointment must be within doctor's availability
        if (!isDoctorAvailable(doctor.getId(), request.getAppointmentDateTime())) {
            throw new ValidationException("Doctor is not available at the requested time");
        }
        
        // Rule 3: Check consultation mode availability
        if (request.getMode() == ConsultationMode.VIDEO && !doctor.getVideoConsultationEnabled()) {
            throw new ValidationException("Doctor does not offer video consultations");
        }
        
        if (request.getMode() == ConsultationMode.TELE && !doctor.getTeleConsultationEnabled()) {
            throw new ValidationException("Doctor does not offer telephonic consultations");
        }
        
        // Rule 4: Maximum 3 upcoming appointments per patient
        long upcomingAppointments = appointmentRepository.countByPatientIdAndStatusAndAppointmentDateTimeAfter(
            request.getPatientId(), AppointmentStatus.CONFIRMED, LocalDateTime.now()
        );
        
        if (upcomingAppointments >= 3) {
            throw new ValidationException("Maximum 3 upcoming appointments allowed per patient");
        }
    }
}

Custom Annotations

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FutureAppointmentValidator.class)
public @interface FutureAppointment {
    String message() default "Appointment must be in the future";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    long hours() default 2;
}

public class FutureAppointmentValidator implements ConstraintValidator<FutureAppointment, LocalDateTime> {
    private long hours;
    
    @Override
    public void initialize(FutureAppointment constraintAnnotation) {
        this.hours = constraintAnnotation.hours();
    }
    
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        return value != null && value.isAfter(LocalDateTime.now().plusHours(hours));
    }
}

Payment Processing Rules

@Service
public class PaymentService {
    
    @Transactional
    public PaymentResponse processPayment(PaymentCheckoutRequest request) {
        // Rule 1: Validate appointment exists and is bookable
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
            .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new ValidationException("Appointment is not in a bookable state");
        }
        
        // Rule 2: Validate payment amount matches consultation fee
        BigDecimal expectedAmount = appointment.getDoctor().getFeePerConsultation();
        if (request.getAmount().compareTo(expectedAmount) != 0) {
            throw new ValidationException("Payment amount does not match consultation fee");
        }
        
        // Rule 3: Process payment through Stripe
        try {
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(request);
            
            // Create payment record
            Payment payment = new Payment();
            payment.setAppointmentId(appointment.getId());
            payment.setAmount(request.getAmount());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setStripePaymentIntentId(paymentIntent.getId());
            
            payment = paymentRepository.save(payment);
            
            return PaymentResponse.builder()
                .paymentId(payment.getId())
                .clientSecret(paymentIntent.getClientSecret())
                .build();
                
        } catch (StripeException e) {
            throw new PaymentProcessingException("Failed to process payment", e);
        }
    }
}

Security Requirements

Spring Security Configuration
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/taxonomy/**", "/api/doctors/search").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/doctors/me/**").hasRole("DOCTOR")
                .requestMatchers("/api/patients/me/**").hasRole("PATIENT")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}


JWT Implementation
@Component
public class JwtUtil {
    
    @Value("${app.jwt.secret}")
    private String secret;
    
    @Value("${app.jwt.expiration}")
    private Long expiration;
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        User user = (User) userDetails;
        claims.put("role", user.getRole().name());
        claims.put("userId", user.getId().toString());
        return createToken(claims, userDetails.getUsername());
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain chain) throws ServletException, IOException {
        
        final String requestTokenHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwtToken = null;
        
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                logger.error("JWT Token has expired");
            }
        }
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            
            if (jwtUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(request, response);
    }
}

Audit Logging

@Component
@Slf4j
public class AuditLogger {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @EventListener
    public void handleUserAuthentication(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        logAction(username, "USER_LOGIN", "User", username, null, null);
    }
    
    @EventListener
    public void handleAppointmentCreated(AppointmentCreatedEvent event) {
        Appointment appointment = event.getAppointment();
        logAction(
            getCurrentUsername(),
            "APPOINTMENT_CREATED",
            "Appointment",
            appointment.getId().toString(),
            null,
            convertToJson(appointment)
        );
    }
    
    private void logAction(String username, String action, String entityType, 
                          String entityId, Object oldValues, Object newValues) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setUserId(getUserIdByUsername(username));
            auditLog.setAction(action);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setOldValues(oldValues != null ? convertToJson(oldValues) : null);
            auditLog.setNewValues(newValues != null ? convertToJson(newValues) : null);
            auditLog.setIpAddress(getCurrentRequestIpAddress());
            auditLog.setUserAgent(getCurrentRequestUserAgent());
            
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to log audit event", e);
        }
    }
}


Third-party Integrations
Stripe Payment Integration

@Service
public class StripeService {
    
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    
    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }
    
    public PaymentIntent createPaymentIntent(PaymentCheckoutRequest request) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount(request.getAmount().multiply(BigDecimal.valueOf(100)).longValue()) // Convert to cents
            .setCurrency("usd")
            .putMetadata("appointmentId", request.getAppointmentId().toString())
            .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                    .setEnabled(true)
                    .build()
            )
            .build();
        
        return PaymentIntent.create(params);
    }
    
    @EventListener
    public void handlePaymentSucceeded(PaymentIntentSucceededEvent event) {
        String appointmentId = event.getPaymentIntent().getMetadata().get("appointmentId");
        paymentService.updatePaymentStatus(UUID.fromString(appointmentId), PaymentStatus.PAID);
        appointmentService.confirmAppointment(UUID.fromString(appointmentId));
    }
}

@RestController
@RequestMapping("/api/payments/stripe")
public class StripeWebhookController {
    
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;
    
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, 
                                               @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
                    handlePaymentSucceeded(paymentIntent);
                    break;
                case "payment_intent.payment_failed":
                    PaymentIntent failedPayment = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
                    handlePaymentFailed(failedPayment);
                    break;
                default:
                    log.info("Unhandled event type: {}", event.getType());
            }
            
            return ResponseEntity.ok("Webhook handled successfully");
        } catch (SignatureVerificationException e) {
            log.error("Invalid signature", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }
    }
}

Email Service Integration
@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    @Value("${app.mail.from}")
    private String fromEmail;
    
    public void sendAppointmentConfirmation(Appointment appointment) {
        try {
            Context context = new Context();
            context.setVariable("patientName", appointment.getPatient().getUser().getFullName());
            context.setVariable("doctorName", appointment.getDoctor().getUser().getFullName());
            context.setVariable("appointmentDate", appointment.getAppointmentDateTime());
            context.setVariable("consultationMode", appointment.getMode());
            
            String htmlContent = templateEngine.process("appointment-confirmation", context);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(appointment.getPatient().getUser().getEmail());
            helper.setSubject("Appointment Confirmation - DocMate");
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
        } catch (MessagingException e) {
            log.error("Failed to send appointment confirmation email", e);
        }
    }
    
    @Async
    public void sendAppointmentReminder(Appointment appointment) {
        // Send reminder 24 hours before appointment
        // Implementation similar to confirmation email
    }
}

File Storage Service

@Service
public class FileStorageService {
    
    @Autowired
    private AmazonS3 amazonS3;
    
    @Value("${aws.s3.bucket}")
    private String bucketName;
    
    public String uploadFile(MultipartFile file, String folder, UUID userId) {
        try {
            String fileName = generateFileName(file.getOriginalFilename(), userId);
            String key = folder + "/" + fileName;
            
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
            putObjectRequest.setCannedAcl(CannedAccessControlList.Private);
            
            amazonS3.putObject(putObjectRequest);
            
            return amazonS3.getUrl(bucketName, key).toString();
            
        } catch (Exception e) {
            throw new FileStorageException("Failed to upload file", e);
        }
    }
    
    public byte[] downloadFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            S3Object s3Object = amazonS3.getObject(bucketName, key);
            return IOUtils.toByteArray(s3Object.getObjectContent());
        } catch (Exception e) {
            throw new FileStorageException("Failed to download file", e);
        }
    }
    
    public void deleteFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            amazonS3.deleteObject(bucketName, key);
        } catch (Exception e) {
            log.error("Failed to delete file: {}", fileUrl, e);
        }
    }
}


Performance & Scalability

Caching Configuration

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        RedisCacheManager.Builder builder = RedisCacheManager
            .RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory())
            .cacheDefaults(cacheConfiguration());
        
        return builder.build();
    }
    
    private RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
}

@Service
public class DoctorService {
    
    @Cacheable(value = "doctors", key = "#specializationId + '_' + #page + '_' + #size")
    public Page<Doctor> findDoctorsBySpecialization(UUID specializationId, Pageable pageable) {
        return doctorRepository.findBySpecializationIdAndIsApprovedAndIsActive(specializationId, true, true, pageable);
    }
    
    @CacheEvict(value = "doctors", allEntries = true)
    public Doctor updateDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }
}


Database Connection Pool

# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/docmate
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# HikariCP settings
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.validation-timeout=5000
spring.datasource.hikari.leak-detection-threshold=60000

# JPA settings
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true


Async Processing


@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("DocMate-Async-");
        executor.initialize();
        return executor;
    }
}

@Service
public class NotificationService {
    
    @Async("taskExecutor")
    public CompletableFuture<Void> sendAppointmentReminders() {
        List<Appointment> upcomingAppointments = appointmentRepository.findAppointmentsForReminder();
        
        for (Appointment appointment : upcomingAppointments) {
            emailService.sendAppointmentReminder(appointment);
            smsService.sendAppointmentReminder(appointment);
        }
        
        return CompletableFuture.completedFuture(null);
    }
}

API Response Standards
@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Map<String, Object> meta;
    private LocalDateTime timestamp;
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data, null, LocalDateTime.now());
    }
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, null, LocalDateTime.now());
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null, LocalDateTime.now());
    }
}

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed").data(errors));
    }
}

// ...existing content...

### Complete DTO Classes (Continued)

```java
// Filter Request DTOs (Continued)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientAppointmentFilterRequest {
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentFilterRequest {
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorPrescriptionFilterRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String patientName;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientPrescriptionFilterRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String doctorName;
}

// Admin Request DTOs
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveDoctorRequest {
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RejectDoctorRequest {
    @NotBlank(message = "Rejection reason is required")
    @Size(max = 1000, message = "Rejection reason must not exceed 1000 characters")
    private String rejectionReason;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotPasswordRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {
    @NotBlank(message = "Token is required")
    private String token;
    
    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyEmailRequest {
    @NotBlank(message = "Token is required")
    private String token;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResendVerificationRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmPaymentRequest {
    @NotNull(message = "Payment intent ID is required")
    private String paymentIntentId;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSimulationRequest {
    @NotNull(message = "Appointment ID is required")
    private UUID appointmentId;
    
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequest {
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
    
    private BigDecimal amount;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailPrescriptionRequest {
    @Email(message = "Email must be valid")
    private String email;
    
    @Size(max = 500, message = "Message must not exceed 500 characters")
    private String message;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRecurringAvailabilityRequest {
    @NotNull(message = "Start time is required")
    private String startTime;
    
    @NotNull(message = "End time is required")
    private String endTime;
    
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
    
    private Boolean isActive;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAddressRequest {
    @NotBlank(message = "Address line 1 is required")
    @Size(max = 200, message = "Address line 1 must not exceed 200 characters")
    private String addressLine1;
    
    @Size(max = 200, message = "Address line 2 must not exceed 200 characters")
    private String addressLine2;
    
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;
    
    @NotBlank(message = "ZIP code is required")
    @Size(max = 20, message = "ZIP code must not exceed 20 characters")
    private String zipCode;
    
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;
    
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean isPrimary;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePrescriptionRequest {
    @NotBlank(message = "Diagnosis is required")
    @Size(max = 1000, message = "Diagnosis must not exceed 1000 characters")
    private String diagnosis;
    
    @Size(max = 1000, message = "Symptoms must not exceed 1000 characters")
    private String symptoms;
    
    @Size(max = 1000, message = "Advice must not exceed 1000 characters")
    private String advice;
    
    @Valid
    @NotEmpty(message = "At least one medicine is required")
    private List<PrescriptionMedicineRequest> medicines;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSpecializationRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSpecializationRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private Boolean isActive;
}
```

### Response DTOs (Complete)

```java
// Response DTOs
package com.docmate.platform.dto.response;

import com.docmate.platform.entity.Appointment.AppointmentStatus;
import com.docmate.platform.entity.Appointment.ConsultationMode;
import com.docmate.platform.entity.Appointment.PaymentStatus;
import com.docmate.platform.entity

// ...existing content...

### Complete Repository Interfaces (Continued)

```java
// PaymentRepository.java (Continued)
@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    
    // ...existing methods...
    
    @Query("SELECT SUM(p.amount) FROM Payment p JOIN p.appointment a " +
           "WHERE a.doctor.id = :doctorId AND p.status = :status " +
           "AND DATE(p.createdDate) BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByDoctorIdAndStatusAndDateRange(@Param("doctorId") UUID doctorId,
                                                      @Param("status") Payment.PaymentStatus status,
                                                      @Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);
    
    @Query("SELECT new com.docmate.platform.dto.response.MonthlyEarningResponse(" +
           "MONTHNAME(p.createdDate), SUM(p.amount), COUNT(p)) " +
           "FROM Payment p JOIN p.appointment a " +
           "WHERE a.doctor.id = :doctorId AND p.status = 'COMPLETED' " +
           "AND DATE(p.createdDate) BETWEEN :startDate AND :endDate " +
           "GROUP BY MONTH(p.createdDate), MONTHNAME(p.createdDate) " +
           "ORDER BY MONTH(p.createdDate)")
    List<MonthlyEarningResponse> getMonthlyEarningsBreakdown(@Param("doctorId") UUID doctorId,
                                                           @Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p FROM Payment p JOIN p.appointment a " +
           "WHERE a.patient.id = :patientId AND p.status = :status " +
           "AND DATE(p.createdDate) BETWEEN :startDate AND :endDate")
    Page<Payment> findByPatientIdAndStatusAndDateRange(@Param("patientId") UUID patientId,
                                                     @Param("status") Payment.PaymentStatus status,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate,
                                                     Pageable pageable);
    
    @Query("SELECT p FROM Payment p JOIN p.appointment a " +
           "WHERE a.doctor.id = :doctorId AND p.status = :status " +
           "AND DATE(p.createdDate) BETWEEN :startDate AND :endDate")
    Page<Payment> findByDoctorIdAndStatusAndDateRange(@Param("doctorId") UUID doctorId,
                                                    @Param("status") Payment.PaymentStatus status,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate,
                                                    Pageable pageable);
}

// SpecializationRepository.java
package com.docmate.platform.repository;

import com.docmate.platform.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, UUID> {
    
    List<Specialization> findByIsActiveTrueOrderByName();
    
    @Query("SELECT s FROM Specialization s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND s.isActive = true")
    List<Specialization> findByNameContainingIgnoreCaseAndIsActiveTrue(@Param("name") String name);
    
    boolean existsByNameIgnoreCase(String name);
    
    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.specialization.id = :specializationId")
    Long countDoctorsBySpecializationId(@Param("specializationId") UUID specializationId);
}

// ConditionRepository.java
package com.docmate.platform.repository;

import com.docmate.platform.entity.Condition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConditionRepository extends JpaRepository<Condition, UUID> {
    
    List<Condition> findByIsActiveTrueOrderByName();
    
    @Query("SELECT c FROM Condition c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND c.isActive = true")
    List<Condition> findByNameContainingIgnoreCaseAndIsActiveTrue(@Param("name") String name);
    
    boolean existsByNameIgnoreCase(String name);
    
    @Query("SELECT COUNT(dc) FROM Doctor d JOIN d.conditions dc WHERE dc.id = :conditionId")
    Long countDoctorsByConditionId(@Param("conditionId") UUID conditionId);
}

// AddressRepository.java
package com.docmate.platform.repository;

import com.docmate.platform.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    
    @Query("SELECT a FROM Address a WHERE a.city = :city AND a.state = :state")
    List<Address> findByCityAndState(@Param("city") String city, @Param("state") String state);
    
    @Query(value = "SELECT * FROM addresses a WHERE " +
           "ST_DWithin(ST_Point(a.longitude, a.latitude), ST_Point(:longitude, :latitude), :radiusKm * 1000) " +
           "ORDER BY ST_Distance(ST_Point(a.longitude, a.latitude), ST_Point(:longitude, :latitude))",
           nativeQuery = true)
    List<Address> findAddressesWithinRadius(@Param("latitude") BigDecimal latitude,
                                          @Param("longitude") BigDecimal longitude,
                                          @Param("radiusKm") Double radiusKm);
}

// ReviewRepository.java
package com.docmate.platform.repository;

import com.docmate.platform.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    
    Page<Review> findByDoctorIdAndIsFlaggedFalse(UUID doctorId, Pageable pageable);
    
    Page<Review> findByPatientId(UUID patientId, Pageable pageable);
    
    Optional<Review> findByAppointmentId(UUID appointmentId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.doctor.id = :doctorId AND r.isFlagged = false")
    BigDecimal getAverageRatingByDoctorId(@Param("doctorId") UUID doctorId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.doctor.id = :doctorId AND r.isFlagged = false")
    Long countByDoctorIdAndIsFlaggedFalse(@Param("doctorId") UUID doctorId);
    
    @Query("SELECT r FROM Review r WHERE r.rating <= 2 AND r.isFlagged = false " +
           "ORDER BY r.createdDate DESC")
    Page<Review> findLowRatingReviews(Pageable pageable);
    
    boolean existsByAppointmentId(UUID appointmentId);
}

// DoctorRecurringAvailabilityRepository.java
package com.docmate.platform.repository;

import com.docmate.platform.entity.DoctorRecurringAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorRecurringAvailabilityRepository extends JpaRepository<DoctorRecurringAvailability, UUID> {
    
    List<DoctorRecurringAvailability> findByDoctorIdAndIsActiveTrue(UUID doctorId);
    
    List<DoctorRecurringAvailability> findByDoctorIdAndDayOfWeekAndIsActiveTrue(UUID doctorId, DayOfWeek dayOfWeek);
    
    @Query("SELECT COUNT(dra) > 0 FROM DoctorRecurringAvailability dra " +
           "WHERE dra.doctor.id = :doctorId " +
           "AND dra.dayOfWeek = :dayOfWeek " +
           "AND dra.isActive = true " +
           "AND ((dra.startTime <= :startTime AND dra.endTime > :startTime) " +
           "OR (dra.startTime < :endTime AND dra.endTime >= :endTime) " +
           "OR (dra.startTime >= :startTime AND dra.endTime <= :endTime))")
    boolean existsByDoctorIdAndDayOfWeekAndTimeOverlap(@Param("doctorId") UUID doctorId,
                                                      @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                                      @Param("startTime") LocalTime startTime,
                                                      @Param("endTime") LocalTime endTime);
}

// DoctorAdhocAvailabilityRepository.java
package com.docmate.platform.repository;

import com.docmate.platform.entity.DoctorAdhocAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorAdhocAvailabilityRepository extends JpaRepository<DoctorAdhocAvailability, UUID> {
    
    List<DoctorAdhocAvailability> findByDoctorIdAndIsActiveTrue(UUID doctorId);
    
    List<DoctorAdhocAvailability> findByDoctorIdAndDateAndIsActiveTrue(UUID doctorId, LocalDate date);
    
    @Query("SELECT daa FROM DoctorAdhocAvailability daa " +
           "WHERE daa.doctor.id = :doctorId " +
           "AND daa.date BETWEEN :startDate AND :endDate " +
           "AND daa.isActive = true " +
           "ORDER BY daa.date, daa.startTime")
    List<DoctorAdhocAvailability> findByDoctorIdAndDateRange(@Param("doctorId") UUID doctorId,
                                                           @Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(daa) > 0 FROM DoctorAdhocAvailability daa " +
           "WHERE daa.doctor.id = :doctorId " +
           "AND daa.date = :date " +
           "AND daa.isActive = true " +
           "AND ((daa.startTime <= :startTime AND daa.endTime > :startTime) " +
           "OR (daa.startTime < :endTime AND daa.endTime >= :endTime) " +
           "OR (daa.startTime >= :startTime AND daa.endTime <= :endTime))")
    boolean existsByDoctorIdAndDateAndTimeOverlap(@Param("doctorId") UUID doctorId,
                                                 @Param("date") LocalDate date,
                                                 @Param("startTime") LocalTime startTime,
                                                 @Param("endTime") LocalTime endTime);
}

// PatientMedicalHistoryRepository.java
package com.docmate.platform.repository;

import com.docmate.platform.entity.PatientMedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientMedicalHistoryRepository extends JpaRepository<PatientMedicalHistory, UUID> {
    
    Optional<PatientMedicalHistory> findByPatientId(UUID patientId);
    
    boolean existsByPatientId(UUID patientId);
}

// PatientReportRepository.java
package com.docmate.platform.repository;

import com.docmate.platform.entity.PatientReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PatientReportRepository extends JpaRepository<PatientReport, UUID> {
    
    Page<PatientReport> findByPatientIdOrderByCreatedDateDesc(UUID patientId, Pageable pageable);
    
    @Query("SELECT pr FROM PatientReport pr WHERE pr.patient.id = :patientId " +
           "AND pr.testDate BETWEEN :startDate AND :endDate " +
           "ORDER BY pr.testDate DESC")
    List<PatientReport> findByPatientIdAndTestDateBetween(@Param("patientId") UUID patientId,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);
    
    @Query("SELECT pr FROM PatientReport pr WHERE pr.patient.id = :patientId " +
           "AND LOWER(pr.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
           "ORDER BY pr.createdDate DESC")
    List<PatientReport> findByPatientIdAndTitleContaining(@Param("patientId") UUID patientId,
                                                        @Param("title") String title);
    
    Long countByPatientId(UUID patientId);
}

// DoctorVerificationDocumentRepository.java
package com.docmate.platform.repository;

import com.docmate.platform.entity.DoctorVerificationDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorVerificationDocumentRepository extends JpaRepository<DoctorVerificationDocument, UUID> {
    
    Optional<DoctorVerificationDocument> findByDoctorId(UUID doctorId);
    
    @Query("SELECT dvd FROM DoctorVerificationDocument dvd " +
           "WHERE dvd.verificationStatus = 'PENDING' " +
           "ORDER BY dvd.createdDate ASC")
    Page<DoctorVerificationDocument> findPendingVerifications(Pageable pageable);
    
    @Query("SELECT COUNT(dvd) FROM DoctorVerificationDocument dvd " +
           "WHERE dvd.verificationStatus = :status")
    Long countByVerificationStatus(@Param("status") DoctorVerificationDocument.VerificationStatus status);
}

// NotificationRepository.java
package com.docmate.platform.repository;

import com.docmate.platform.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    
    Page<Notification> findByUserIdOrderByCreatedDateDesc(UUID userId, Pageable pageable);
    
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedDateDesc(UUID userId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    Long countUnreadByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId " +
           "AND n.type = :type ORDER BY n.createdDate DESC")
    List<Notification> findByUserIdAndType(@Param("userId") UUID userId, @Param("type") String type);
}

// AuditLogRepository.java
package com.docmate.platform.repository;

import com.docmate.platform.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    
    Page<AuditLog> findByUserIdOrderByCreatedDateDesc(UUID userId, Pageable pageable);
    
    Page<AuditLog> findByEntityTypeOrderByCreatedDateDesc(String entityType, Pageable pageable);
    
    @Query("SELECT al FROM AuditLog al WHERE al.action = :action " +
           "AND al.createdDate BETWEEN :startDate AND :endDate " +
           "ORDER BY al.createdDate DESC")
    List<AuditLog> findByActionAndDateRange(@Param("action") String action,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT al FROM AuditLog al WHERE al.entityType = :entityType " +
           "AND al.entityId = :entityId ORDER BY al.createdDate DESC")
    List<AuditLog> findByEntityTypeAndEntityId(@Param("entityType") String entityType,
                                             @Param("entityId") String entityId);
}
```

### Complete Service Layer (Continued)

```java
// PatientService.java
package com.docmate.platform.service;

import com.docmate.platform.dto.request.*;
import com.docmate.platform.dto.response.*;
import com.docmate.platform.entity.*;
import com.docmate.platform.mapper.PatientMapper;
import com.docmate.platform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientService {
    
    private final PatientRepository patientRepository;
    private final PatientMedicalHistoryRepository medicalHistoryRepository;
    private final PatientReportRepository patientReportRepository;
    private final AddressRepository addressRepository;
    private final UserService userService;
    private final PatientMapper patientMapper;
    private final FileStorageService fileStorageService;
    
    @Transactional(readOnly = true)
    public PatientProfileResponse getPatientProfile(String email) {
        User user = userService.getUserByEmail(email);
        Patient patient = patientRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient profile not found"));
        
        return patientMapper.toPatientProfileResponse(patient);
    }
    
    public PatientProfileResponse updatePatientProfile(String email, UpdatePatientProfileRequest request) {
        User user = userService.getUserByEmail(email);
        Patient patient = patientRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient profile not found"));
        
        // Update user fields
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setProfilePicture(request.getProfilePicture());
        
        // Update patient fields
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setBloodType(request.getBloodType());
        patient.setHeight(request.getHeight());
        patient.setWeight(request.getWeight());
        
        patient = patientRepository.save(patient);
        
        log.info("Patient profile updated for {}", email);
        
        return patientMapper.toPatientProfileResponse(patient);
    }
    
    @Transactional(readOnly = true)
    public PatientMedicalHistoryResponse getMedicalHistory(String email) {
        User user = userService.getUserByEmail(email);
        Patient patient = patientRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient profile not found"));
        
        PatientMedicalHistory medicalHistory = medicalHistoryRepository.findByPatientId(patient.getId())
                .orElseGet(() -> PatientMedicalHistory.builder()
                        .patient(patient)
                        .build());
        
        return patientMapper.toPatientMedicalHistoryResponse(medicalHistory);
    }
    
    public PatientMedicalHistoryResponse updateMedicalHistory(String email, 
                                                            UpdatePatientMedicalHistoryRequest request) {
        User user = userService.getUserByEmail(email);
        Patient patient = patientRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient profile not found"));
        
        PatientMedicalHistory medicalHistory = medicalHistoryRepository.findByPatientId(patient.getId())
                .orElse(PatientMedicalHistory.builder()
                        .patient(patient)
                        .build());
        
        medicalHistory.setAllergies(request.getAllergies());
        medicalHistory.setChronicConditions(request.getChronicConditions());
        medicalHistory.setCurrentMedications(request.getCurrentMedications());
        medicalHistory.setPreviousSurgeries(request.getPreviousSurgeries());
        medicalHistory.setFamilyHistory(request.getFamilyHistory());
        medicalHistory.setEmergencyContactName(request.getEmergencyContactName());
        medicalHistory.setEmergencyContactPhone(request.getEmergencyContactPhone());
        
        medicalHistory = medicalHistoryRepository.save(medicalHistory);
        
        log.info("Medical history updated for patient {}", email);
        
        return patientMapper.toPatientMedicalHistoryResponse(medicalHistory);
    }
    
    @Transactional(readOnly = true)
    public PagedResponse<PatientReportResponse> getPatientReports(String email, Pageable pageable) {
        User user = userService.getUserByEmail(email);
        Patient patient = patientRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient profile not found"));
        
        Page<PatientReport> reports = patientReportRepository.findByPatientIdOrderByCreatedDateDesc(
                patient.getId(), pageable);
        
        List<PatientReportResponse> content = reports.getContent().stream()
                .map(patientMapper::toPatientReportResponse)
                .toList();
        
        return PagedResponse.<PatientReportResponse>builder()
                .content(content)
                .page(reports.getNumber())
                .size(reports.getSize())
                .totalElements(reports.getTotalElements())
                .totalPages(reports.getTotalPages())
                .first(reports.isFirst())
                .last(reports.isLast())
                .hasNext(reports.hasNext())
                .hasPrevious(reports.hasPrevious())
                .build();
    }
    
    public PatientReportResponse uploadPatientReport(String email, UploadPatientReportRequest request) {
        User user = userService.getUserByEmail(email);
        Patient patient = patientRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient profile not found"));
        
        // Validate file
        if (request.getFile().isEmpty()) {
            throw new ValidationException("File cannot be empty");
        }
        
        if (request.getFile().getSize() > 10 * 1024 * 1024) { // 10MB limit
            throw new ValidationException("File size cannot exceed 10MB");
        }
        
        // Upload file to storage
        String fileUrl = fileStorageService.uploadFile(
                request.getFile(), "patient-reports", user.getId());
        
        PatientReport report = PatientReport.builder()
                .patient(patient)
                .title(request.getTitle())
                .description(request.getDescription())
                .testDate(request.getTestDate())
                .fileUrl(fileUrl)
                .fileName(request.getFile().getOriginalFilename())
                .fileType(request.getFile().getContentType())
                .fileSize(request.getFile().getSize())
                .build();
        
        report = patientReportRepository.save(report);
        
        log.info("Patient report uploaded for {}", email);
        
        return patientMapper.toPatientReportResponse(report);
    }
    
    public ResponseEntity<byte[]> downloadPatientReport(String email, UUID reportId) {
        User user = userService.getUserByEmail(email);
        Patient patient = patientRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient profile not found"));
        
        PatientReport report = patientReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));
        
        if (!report.getPatient().getId().equals(patient.getId())) {
            throw new AccessDeniedException("Access denied to this report");
        }
        
        byte[] fileContent = fileStorageService.downloadFile(report.getFileUrl());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(report.getFileType()));
        headers.setContentDispositionFormData("attachment", report.getFileName());
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    }
    
    public void deletePatientReport(String email, UUID reportId) {
        User user = userService.getUserByEmail(email);
        Patient patient = patientRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient profile not found"));
        
        PatientReport report = patientReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));
        
        if (!report.getPatient().getId().equals(patient.getId())) {
            throw new AccessDeniedException("Access denied to this report");
        }
        
        // Delete file from storage
        fileStorageService.deleteFile(report.getFileUrl());
        
        // Delete report record
        patientReportRepository.delete(report);
        
        log.info("Patient report deleted for {}", email);
    }
    
    @Transactional(readOnly = true)
    public PagedResponse<AddressResponse> getPatientAddresses(String email, Pageable pageable) {
        User user = userService.getUserByEmail(email);
        
        // Implementation would need to add patient-address relationship
        // For now, return empty response
        return PagedResponse.<AddressResponse>builder()
                .content(List.of())
                .page(0)
                .size(0)
                .totalElements(0L)
                .totalPages(0)
                .first(true)
                .last(true)
                .hasNext(false)
                .hasPrevious(false)
                .build();
    }
    
    public AddressResponse addPatientAddress(String email, CreateAddressRequest request) {
        User user = userService.getUserByEmail(email);
        
        Address address = Address.builder()
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .country(request.getCountry())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .isPrimary(request.getIsPrimary())
                .build();
        
        address = addressRepository.save(address);
        
        log.info("Address added for patient {}", email);
        
        return patientMapper.toAddressResponse(address);
    }
    
    public AddressResponse updatePatientAddress(String email, UUID addressId, UpdateAddressRequest request) {
        User user = userService.getUserByEmail(email);
        
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));
        
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setCountry(request.getCountry());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setIsPrimary(request.getIsPrimary());
        
        address = addressRepository.save(address);
        
        log.info("Address updated for patient {}", email);
        
        return patientMapper.toAddressResponse(address);
    }
    
    public void deletePatientAddress(String email, UUID addressId) {
        User user = userService.getUserByEmail(email);
        
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));
        
        address.setIsPrimary(false);
        addressRepository.save(address);
        
        log.info("Address deleted for patient {}", email);
    }
}






