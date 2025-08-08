package com.docmate.common.entity;

import com.docmate.common.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends BaseEntity {
    
    @Id
    @Column(name = "id")
    private UUID id;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;
    
    @Size(max = 5, message = "Blood type must not exceed 5 characters")
    @Column(name = "blood_type", length = 5)
    private String bloodType;
    
    @Size(max = 10, message = "Height must not exceed 10 characters")
    @Column(name = "height", length = 10)
    private String height;
    
    @Size(max = 10, message = "Weight must not exceed 10 characters")
    @Column(name = "weight", length = 10)
    private String weight;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;
}
