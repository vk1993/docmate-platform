package com.docmate.common.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePatientMedicalHistoryRequest {

    @Size(max = 2000, message = "Allergies must not exceed 2000 characters")
    private String allergies;

    @Size(max = 2000, message = "Chronic conditions must not exceed 2000 characters")
    private String chronicConditions;

    @Size(max = 2000, message = "Current medications must not exceed 2000 characters")
    private String currentMedications;

    @Size(max = 2000, message = "Previous surgeries must not exceed 2000 characters")
    private String previousSurgeries;

    @Size(max = 2000, message = "Family history must not exceed 2000 characters")
    private String familyHistory;

    @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
    private String emergencyContactName;

    @Size(max = 20, message = "Emergency contact phone must not exceed 20 characters")
    private String emergencyContactPhone;
}
