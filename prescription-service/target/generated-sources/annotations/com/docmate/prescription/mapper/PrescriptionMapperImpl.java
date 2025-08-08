package com.docmate.prescription.mapper;

import com.docmate.common.entity.CommonAppointment;
import com.docmate.common.entity.Doctor;
import com.docmate.common.entity.Patient;
import com.docmate.common.entity.Prescription;
import com.docmate.prescription.dto.PrescriptionDto;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-08T21:59:55+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class PrescriptionMapperImpl implements PrescriptionMapper {

    @Override
    public PrescriptionDto toDto(Prescription prescription) {
        if ( prescription == null ) {
            return null;
        }

        PrescriptionDto.PrescriptionDtoBuilder prescriptionDto = PrescriptionDto.builder();

        prescriptionDto.appointmentId( prescriptionAppointmentId( prescription ) );
        prescriptionDto.doctorId( prescriptionDoctorId( prescription ) );
        prescriptionDto.patientId( prescriptionPatientId( prescription ) );
        prescriptionDto.id( prescription.getId() );
        prescriptionDto.diagnosis( prescription.getDiagnosis() );
        prescriptionDto.symptoms( prescription.getSymptoms() );
        prescriptionDto.advice( prescription.getAdvice() );
        prescriptionDto.createdDate( prescription.getCreatedDate() );

        return prescriptionDto.build();
    }

    private UUID prescriptionAppointmentId(Prescription prescription) {
        if ( prescription == null ) {
            return null;
        }
        CommonAppointment appointment = prescription.getAppointment();
        if ( appointment == null ) {
            return null;
        }
        UUID id = appointment.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID prescriptionDoctorId(Prescription prescription) {
        if ( prescription == null ) {
            return null;
        }
        Doctor doctor = prescription.getDoctor();
        if ( doctor == null ) {
            return null;
        }
        UUID id = doctor.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID prescriptionPatientId(Prescription prescription) {
        if ( prescription == null ) {
            return null;
        }
        Patient patient = prescription.getPatient();
        if ( patient == null ) {
            return null;
        }
        UUID id = patient.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
