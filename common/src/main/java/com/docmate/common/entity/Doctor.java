package com.docmate.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor extends BaseEntity {
    
    @Id
    @Column(name = "id")
    private UUID id;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;
    
    @NotBlank(message = "License number is required")
    @Size(max = 50, message = "License number must not exceed 50 characters")
    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;
    
    @Builder.Default
    @Column(name = "experience_years")
    private Integer experienceYears = 0;
    
    @Builder.Default
    @Column(name = "fee_per_consultation", precision = 10, scale = 2)
    private BigDecimal feePerConsultation = BigDecimal.ZERO;
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    @Builder.Default
    @Column(name = "video_consultation_enabled")
    private Boolean videoConsultationEnabled = false;
    
    @Builder.Default
    @Column(name = "tele_consultation_enabled")
    private Boolean teleConsultationEnabled = false;
    
    @Builder.Default
    @Column(name = "emergency_available")
    private Boolean emergencyAvailable = false;
    
    @Builder.Default
    @Column(name = "is_approved")
    private Boolean isApproved = false;
    
    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Size(max = 200, message = "Clinic name must not exceed 200 characters")
    @Column(name = "clinic_name", length = 200)
    private String clinicName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_address_id")
    private Address primaryAddress;
    
    @Builder.Default
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "review_count")
    private Integer reviewCount = 0;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "doctor_specializations",
        joinColumns = @JoinColumn(name = "doctor_id"),
        inverseJoinColumns = @JoinColumn(name = "specialization_id")
    )
    @Builder.Default
    private Set<Specialization> specializations = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "doctor_conditions",
        joinColumns = @JoinColumn(name = "doctor_id"),
        inverseJoinColumns = @JoinColumn(name = "condition_id")
    )
    @Builder.Default
    private Set<Condition> conditions = new HashSet<>();
}
