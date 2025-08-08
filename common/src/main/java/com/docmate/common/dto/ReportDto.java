package com.docmate.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReportDto(
    UUID id,
    String title,
    String fileName,
    String fileType,
    Long fileSize,
    String description,
    String category,
    UUID patientId,
    UUID doctorId,
    UUID appointmentId,
    String reportType,
    LocalDateTime uploadedAt,
    LocalDateTime reportDate,
    Boolean isPublic
) {}
