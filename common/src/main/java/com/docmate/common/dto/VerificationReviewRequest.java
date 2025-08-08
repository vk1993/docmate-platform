package com.docmate.common.dto;

import com.docmate.common.enums.VerificationStatus;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationReviewRequest {

    @NotNull(message = "Verification ID is required")
    private UUID verificationId;

    @NotNull(message = "Verification status is required")
    private VerificationStatus verificationStatus;

    private String rejectionReason; // Required if status is REJECTED
}
