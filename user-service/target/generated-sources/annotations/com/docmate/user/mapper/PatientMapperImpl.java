package com.docmate.user.mapper;

import com.docmate.common.dto.AddressDto;
import com.docmate.common.dto.PatientDto;
import com.docmate.common.dto.PatientProfileDto;
import com.docmate.common.entity.Patient;
import com.docmate.common.entity.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-08T21:59:52+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class PatientMapperImpl implements PatientMapper {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AddressMapper addressMapper;

    @Override
    public PatientDto toDto(Patient patient) {
        if ( patient == null ) {
            return null;
        }

        PatientDto.PatientDtoBuilder patientDto = PatientDto.builder();

        patientDto.user( userMapper.toDto( patient.getUser() ) );
        patientDto.address( addressMapper.toDto( patient.getAddress() ) );
        patientDto.id( patient.getId() );
        patientDto.dateOfBirth( patient.getDateOfBirth() );
        patientDto.gender( patient.getGender() );
        patientDto.bloodType( patient.getBloodType() );
        patientDto.height( patient.getHeight() );
        patientDto.weight( patient.getWeight() );
        patientDto.createdDate( patient.getCreatedDate() );
        patientDto.updatedDate( patient.getUpdatedDate() );

        return patientDto.build();
    }

    @Override
    public Patient toEntity(PatientDto patientDto) {
        if ( patientDto == null ) {
            return null;
        }

        Patient.PatientBuilder patient = Patient.builder();

        patient.dateOfBirth( patientDto.getDateOfBirth() );
        patient.gender( patientDto.getGender() );
        patient.bloodType( patientDto.getBloodType() );
        patient.height( patientDto.getHeight() );
        patient.weight( patientDto.getWeight() );

        return patient.build();
    }

    @Override
    public PatientProfileDto toProfileDto(Patient patient) {
        if ( patient == null ) {
            return null;
        }

        String fullName = null;
        String email = null;
        String phone = null;
        String dateOfBirth = null;
        String gender = null;
        String bloodType = null;
        String height = null;
        String weight = null;
        AddressDto address = null;

        fullName = patientUserFullName( patient );
        email = patientUserEmail( patient );
        phone = patientUserPhone( patient );
        if ( patient.getDateOfBirth() != null ) {
            dateOfBirth = DateTimeFormatter.ISO_LOCAL_DATE.format( patient.getDateOfBirth() );
        }
        if ( patient.getGender() != null ) {
            gender = patient.getGender().name();
        }
        bloodType = patient.getBloodType();
        height = patient.getHeight();
        weight = patient.getWeight();
        address = addressMapper.toDto( patient.getAddress() );

        UUID id = null;
        String medicalHistory = null;
        String emergencyContactName = null;
        String emergencyContactPhone = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        PatientProfileDto patientProfileDto = new PatientProfileDto( id, fullName, email, phone, dateOfBirth, gender, bloodType, height, weight, medicalHistory, emergencyContactName, emergencyContactPhone, address, createdAt, updatedAt );

        return patientProfileDto;
    }

    @Override
    public void updateEntityFromDto(PatientDto patientDto, Patient patient) {
        if ( patientDto == null ) {
            return;
        }

        patient.setDateOfBirth( patientDto.getDateOfBirth() );
        patient.setGender( patientDto.getGender() );
        patient.setBloodType( patientDto.getBloodType() );
        patient.setHeight( patientDto.getHeight() );
        patient.setWeight( patientDto.getWeight() );
        patient.setAddress( addressMapper.toEntity( patientDto.getAddress() ) );
    }

    private String patientUserFullName(Patient patient) {
        if ( patient == null ) {
            return null;
        }
        User user = patient.getUser();
        if ( user == null ) {
            return null;
        }
        String fullName = user.getFullName();
        if ( fullName == null ) {
            return null;
        }
        return fullName;
    }

    private String patientUserEmail(Patient patient) {
        if ( patient == null ) {
            return null;
        }
        User user = patient.getUser();
        if ( user == null ) {
            return null;
        }
        String email = user.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }

    private String patientUserPhone(Patient patient) {
        if ( patient == null ) {
            return null;
        }
        User user = patient.getUser();
        if ( user == null ) {
            return null;
        }
        String phone = user.getPhone();
        if ( phone == null ) {
            return null;
        }
        return phone;
    }
}
