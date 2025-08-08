package com.docmate.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientMedicalHistoryResponse {

    private UUID id;
    private UUID patientId;
    private String allergies;
    private String chronicConditions;
    private String currentMedications;
    private String previousSurgeries;
    private String familyHistory;
    private String emergencyContactName;
    private String emergencyContactPhone;
}
