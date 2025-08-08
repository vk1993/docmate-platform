package com.docmate.file.service;

import com.docmate.common.exception.BusinessException;
import com.docmate.file.dto.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name:docmate-files}")
    private String bucketName;

    @Value("${aws.s3.base-url:https://docmate-files.s3.amazonaws.com}")
    private String baseUrl;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    );

    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
            "application/pdf", "image/jpeg", "image/jpg", "image/png"
    );

    public FileUploadResponse uploadProfilePicture(MultipartFile file, UUID userId) {
        log.info("Uploading profile picture for user: {}", userId);

        validateImageFile(file);
        String fileName = generateFileName("profile", userId, file.getOriginalFilename());
        String fileUrl = uploadToS3(file, fileName);

        return FileUploadResponse.builder()
                .fileId(UUID.randomUUID())
                .fileName(fileName)
                .fileUrl(fileUrl)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .uploadStatus("SUCCESS")
                .build();
    }

    public FileUploadResponse uploadMedicalReport(MultipartFile file, String title, String description, UUID patientId) {
        log.info("Uploading medical report for patient: {}", patientId);

        validateDocumentFile(file);
        String fileName = generateFileName("medical-report", patientId, file.getOriginalFilename());
        String fileUrl = uploadToS3(file, fileName);

        return FileUploadResponse.builder()
                .fileId(UUID.randomUUID())
                .fileName(fileName)
                .fileUrl(fileUrl)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .uploadStatus("SUCCESS")
                .build();
    }

    public FileUploadResponse uploadDoctorVerificationDocument(MultipartFile file, String documentType, UUID doctorId) {
        log.info("Uploading verification document for doctor: {}", doctorId);

        validateDocumentFile(file);
        String fileName = generateFileName("verification", doctorId, Objects.requireNonNull(file.getOriginalFilename()));
        String fileUrl = uploadToS3(file, fileName);

        return FileUploadResponse.builder()
                .fileId(UUID.randomUUID())
                .fileName(fileName)
                .fileUrl(fileUrl)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .uploadStatus("SUCCESS")
                .build();
    }

    public Resource downloadFile(UUID fileId, UUID userId) {
        log.info("Downloading file {} for user: {}", fileId, userId);
        // In a real implementation, this would retrieve the file from S3
        throw new BusinessException("DOWNLOAD_NOT_IMPLEMENTED", "File download not implemented", 501);
    }

    public void deleteFile(UUID fileId, UUID userId) {
        log.info("Deleting file {} for user: {}", fileId, userId);

        try {
            // In a real implementation, you would retrieve the file path from database
            String fileName = "mock-file-path";

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("File deleted successfully from S3: {}", fileName);

        } catch (Exception e) {
            log.error("Error deleting file from S3: {}", e.getMessage());
            throw new BusinessException("FILE_DELETE_FAILED", "Failed to delete file", 500);
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("EMPTY_FILE", "File is empty", 400);
        }

        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new BusinessException("INVALID_FILE_TYPE", "Only image files are allowed", 400);
        }

        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new BusinessException("FILE_TOO_LARGE", "File size must not exceed 5MB", 400);
        }
    }

    private void validateDocumentFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("EMPTY_FILE", "File is empty", 400);
        }

        if (!ALLOWED_DOCUMENT_TYPES.contains(file.getContentType())) {
            throw new BusinessException("INVALID_FILE_TYPE", "Only PDF and image files are allowed", 400);
        }

        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new BusinessException("FILE_TOO_LARGE", "File size must not exceed 10MB", 400);
        }
    }

    private String generateFileName(String prefix, UUID userId, String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return String.format("%s/%s/%s%s", prefix, userId, UUID.randomUUID(), extension);
    }

    private String uploadToS3(MultipartFile file, String fileName) {
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));

            String fileUrl = String.format("%s/%s", baseUrl, fileName);
            log.info("File uploaded successfully to S3: {}", fileUrl);

            return fileUrl;

        } catch (Exception e) {
            log.error("Error uploading file to S3: {}", e.getMessage());
            throw new BusinessException("FILE_UPLOAD_FAILED", "Failed to upload file", 500);
        }
    }
}
