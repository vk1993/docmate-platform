package com.docmate.file.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileUploadResponse {
    
    private UUID fileId;
    private String fileName;
    private String fileUrl;
    private String contentType;
    private Long fileSize;
    private String uploadStatus;
}
