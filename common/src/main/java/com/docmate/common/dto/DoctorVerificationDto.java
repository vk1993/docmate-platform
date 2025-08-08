package com.docmate.common.dto;

import com.docmate.common.enums.VerificationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorVerificationDto {
    
    private UUID id;
    private UUID doctorId;
    private String doctorName;
    private String licenseDocumentUrl;
    private String idDocumentUrl;
    private VerificationStatus verificationStatus;
    private UUID verifiedById;
    private String verifiedByName;
    private LocalDateTime verifiedDate;
    private String rejectionReason;
    private LocalDateTime submittedDate;
}
