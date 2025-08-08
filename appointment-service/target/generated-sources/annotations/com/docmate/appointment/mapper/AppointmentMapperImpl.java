package com.docmate.appointment.mapper;

import com.docmate.appointment.dto.AppointmentDto;
import com.docmate.appointment.entity.Appointment;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-08T21:59:53+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class AppointmentMapperImpl implements AppointmentMapper {

    @Override
    public AppointmentDto toDto(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }

        AppointmentDto.AppointmentDtoBuilder appointmentDto = AppointmentDto.builder();

        appointmentDto.id( appointment.getId() );
        appointmentDto.appointmentDateTime( appointment.getAppointmentDateTime() );
        appointmentDto.consultationMode( appointment.getConsultationMode() );
        appointmentDto.status( appointment.getStatus() );
        appointmentDto.durationMinutes( appointment.getDurationMinutes() );
        appointmentDto.consultationFee( appointment.getConsultationFee() );
        appointmentDto.reasonForVisit( appointment.getReasonForVisit() );
        appointmentDto.symptoms( appointment.getSymptoms() );
        appointmentDto.notes( appointment.getNotes() );
        appointmentDto.prescriptionId( appointment.getPrescriptionId() );
        appointmentDto.followUpRequired( appointment.getFollowUpRequired() );
        appointmentDto.followUpDate( appointment.getFollowUpDate() );
        appointmentDto.cancelledReason( appointment.getCancelledReason() );
        appointmentDto.cancelledBy( appointment.getCancelledBy() );
        appointmentDto.cancelledAt( appointment.getCancelledAt() );
        appointmentDto.completedAt( appointment.getCompletedAt() );
        appointmentDto.rating( appointment.getRating() );
        appointmentDto.review( appointment.getReview() );
        appointmentDto.paymentId( appointment.getPaymentId() );
        appointmentDto.meetingLink( appointment.getMeetingLink() );
        appointmentDto.meetingId( appointment.getMeetingId() );
        appointmentDto.createdDate( appointment.getCreatedDate() );
        appointmentDto.updatedDate( appointment.getUpdatedDate() );

        return appointmentDto.build();
    }

    @Override
    public Appointment toEntity(AppointmentDto appointmentDto) {
        if ( appointmentDto == null ) {
            return null;
        }

        Appointment.AppointmentBuilder appointment = Appointment.builder();

        appointment.appointmentDateTime( appointmentDto.getAppointmentDateTime() );
        appointment.status( appointmentDto.getStatus() );
        appointment.consultationMode( appointmentDto.getConsultationMode() );
        appointment.durationMinutes( appointmentDto.getDurationMinutes() );
        appointment.consultationFee( appointmentDto.getConsultationFee() );
        appointment.reasonForVisit( appointmentDto.getReasonForVisit() );
        appointment.symptoms( appointmentDto.getSymptoms() );
        appointment.notes( appointmentDto.getNotes() );
        appointment.prescriptionId( appointmentDto.getPrescriptionId() );
        appointment.followUpRequired( appointmentDto.getFollowUpRequired() );
        appointment.followUpDate( appointmentDto.getFollowUpDate() );
        appointment.cancelledReason( appointmentDto.getCancelledReason() );
        appointment.cancelledBy( appointmentDto.getCancelledBy() );
        appointment.cancelledAt( appointmentDto.getCancelledAt() );
        appointment.completedAt( appointmentDto.getCompletedAt() );
        appointment.rating( appointmentDto.getRating() );
        appointment.review( appointmentDto.getReview() );
        appointment.paymentId( appointmentDto.getPaymentId() );
        appointment.meetingLink( appointmentDto.getMeetingLink() );
        appointment.meetingId( appointmentDto.getMeetingId() );

        return appointment.build();
    }
}
