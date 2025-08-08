package com.docmate.file.controller;

import com.docmate.common.dto.response.ApiResponse;
import com.docmate.common.entity.User;
import com.docmate.file.dto.FileUploadResponse;
import com.docmate.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "File Management", description = "File upload and management APIs")
@SecurityRequirement(name = "bearerAuth")
public class FileController {
    
    private final FileService fileService;
    
    @PostMapping("/upload/profile-picture")
    @Operation(summary = "Upload profile picture", description = "Upload user profile picture")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User currentUser) {
        FileUploadResponse response = fileService.uploadProfilePicture(file, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Profile picture uploaded successfully", response));
    }
    
    @PostMapping("/upload/medical-report")
    @Operation(summary = "Upload medical report", description = "Upload patient medical report")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadMedicalReport(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @AuthenticationPrincipal User currentUser) {
        FileUploadResponse response = fileService.uploadMedicalReport(file, title, description, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Medical report uploaded successfully", response));
    }
    
    @PostMapping("/upload/doctor-verification")
    @Operation(summary = "Upload doctor verification document", description = "Upload doctor license or ID document")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadDoctorVerificationDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @AuthenticationPrincipal User currentUser) {
        FileUploadResponse response = fileService.uploadDoctorVerificationDocument(file, documentType, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Verification document uploaded successfully", response));
    }
    
    @GetMapping("/download/{fileId}")
    @Operation(summary = "Download file", description = "Download file by ID")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable UUID fileId,
            @AuthenticationPrincipal User currentUser) {
        Resource resource = fileService.downloadFile(fileId, currentUser.getId());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    @DeleteMapping("/{fileId}")
    @Operation(summary = "Delete file", description = "Delete file by ID")
    public ResponseEntity<ApiResponse<String>> deleteFile(
            @PathVariable UUID fileId,
            @AuthenticationPrincipal User currentUser) {
        fileService.deleteFile(fileId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("File deleted successfully", "File removed"));
    }
}
