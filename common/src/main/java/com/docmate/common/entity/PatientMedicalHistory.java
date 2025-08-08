package com.docmate.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "patient_medical_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientMedicalHistory extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;
    
    @Column(name = "chronic_conditions", columnDefinition = "TEXT")
    private String chronicConditions;
    
    @Column(name = "current_medications", columnDefinition = "TEXT")
    private String currentMedications;
    
    @Column(name = "previous_surgeries", columnDefinition = "TEXT")
    private String previousSurgeries;
    
    @Column(name = "family_history", columnDefinition = "TEXT")
    private String familyHistory;
    
    @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;
    
    @Size(max = 15, message = "Emergency contact phone must not exceed 15 characters")
    @Column(name = "emergency_contact_phone", length = 15)
    private String emergencyContactPhone;
}
