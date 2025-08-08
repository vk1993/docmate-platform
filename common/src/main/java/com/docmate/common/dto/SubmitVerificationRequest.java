package com.docmate.common.dto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitVerificationRequest {
    
    @NotNull(message = "Doctor ID is required")
    private UUID doctorId;
    
    @NotBlank(message = "License document URL is required")
    private String licenseDocumentUrl;
    
    @NotBlank(message = "ID document URL is required")
    private String idDocumentUrl;
}
