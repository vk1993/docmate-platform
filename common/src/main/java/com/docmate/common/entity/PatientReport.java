package com.docmate.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "patient_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientReport extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @NotBlank(message = "Report title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "test_date")
    private LocalDate testDate;
    
    @Column(name = "file_url", columnDefinition = "TEXT")
    private String fileUrl;
    
    @Size(max = 500, message = "File name must not exceed 500 characters")
    @Column(name = "file_name", length = 500)
    private String fileName;
    
    @Size(max = 100, message = "File type must not exceed 100 characters")
    @Column(name = "file_type", length = 100)
    private String fileType;
    
    @Column(name = "file_size")
    private Long fileSize;
}
