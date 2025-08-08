package com.docmate.user.mapper;

import com.docmate.common.dto.ConditionDto;
import com.docmate.common.dto.DoctorDto;
import com.docmate.common.dto.SpecializationDto;
import com.docmate.common.entity.Condition;
import com.docmate.common.entity.Doctor;
import com.docmate.common.entity.Specialization;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-08T21:59:52+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class DoctorMapperImpl implements DoctorMapper {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SpecializationMapper specializationMapper;
    @Autowired
    private ConditionMapper conditionMapper;
    @Autowired
    private AddressMapper addressMapper;

    @Override
    public DoctorDto toDto(Doctor doctor) {
        if ( doctor == null ) {
            return null;
        }

        DoctorDto.DoctorDtoBuilder doctorDto = DoctorDto.builder();

        doctorDto.user( userMapper.toDto( doctor.getUser() ) );
        doctorDto.specialization( specializationMapper.toDto( doctor.getSpecialization() ) );
        doctorDto.primaryAddress( addressMapper.toDto( doctor.getPrimaryAddress() ) );
        doctorDto.specializations( specializationSetToSpecializationDtoSet( doctor.getSpecializations() ) );
        doctorDto.conditions( conditionSetToConditionDtoSet( doctor.getConditions() ) );
        doctorDto.id( doctor.getId() );
        doctorDto.licenseNumber( doctor.getLicenseNumber() );
        doctorDto.experienceYears( doctor.getExperienceYears() );
        doctorDto.feePerConsultation( doctor.getFeePerConsultation() );
        doctorDto.bio( doctor.getBio() );
        doctorDto.videoConsultationEnabled( doctor.getVideoConsultationEnabled() );
        doctorDto.teleConsultationEnabled( doctor.getTeleConsultationEnabled() );
        doctorDto.emergencyAvailable( doctor.getEmergencyAvailable() );
        doctorDto.isApproved( doctor.getIsApproved() );
        doctorDto.isActive( doctor.getIsActive() );
        doctorDto.clinicName( doctor.getClinicName() );
        doctorDto.averageRating( doctor.getAverageRating() );
        doctorDto.reviewCount( doctor.getReviewCount() );
        doctorDto.createdDate( doctor.getCreatedDate() );
        doctorDto.updatedDate( doctor.getUpdatedDate() );

        return doctorDto.build();
    }

    @Override
    public Doctor toEntity(DoctorDto doctorDto) {
        if ( doctorDto == null ) {
            return null;
        }

        Doctor.DoctorBuilder doctor = Doctor.builder();

        doctor.licenseNumber( doctorDto.getLicenseNumber() );
        doctor.experienceYears( doctorDto.getExperienceYears() );
        doctor.feePerConsultation( doctorDto.getFeePerConsultation() );
        doctor.bio( doctorDto.getBio() );
        doctor.videoConsultationEnabled( doctorDto.getVideoConsultationEnabled() );
        doctor.teleConsultationEnabled( doctorDto.getTeleConsultationEnabled() );
        doctor.emergencyAvailable( doctorDto.getEmergencyAvailable() );
        doctor.isApproved( doctorDto.getIsApproved() );
        doctor.isActive( doctorDto.getIsActive() );
        doctor.clinicName( doctorDto.getClinicName() );

        return doctor.build();
    }

    protected Set<SpecializationDto> specializationSetToSpecializationDtoSet(Set<Specialization> set) {
        if ( set == null ) {
            return null;
        }

        Set<SpecializationDto> set1 = new LinkedHashSet<SpecializationDto>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Specialization specialization : set ) {
            set1.add( specializationMapper.toDto( specialization ) );
        }

        return set1;
    }

    protected Set<ConditionDto> conditionSetToConditionDtoSet(Set<Condition> set) {
        if ( set == null ) {
            return null;
        }

        Set<ConditionDto> set1 = new LinkedHashSet<ConditionDto>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Condition condition : set ) {
            set1.add( conditionMapper.toDto( condition ) );
        }

        return set1;
    }
}
