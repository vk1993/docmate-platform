package com.docmate.prescription.repository;

import com.docmate.common.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    
    @Query("SELECT p FROM Prescription p WHERE p.patient.id = :patientId ORDER BY p.createdDate DESC")
    List<Prescription> findByPatientId(@Param("patientId") UUID patientId);
    
    @Query("SELECT p FROM Prescription p WHERE p.doctor.id = :doctorId ORDER BY p.createdDate DESC")
    List<Prescription> findByDoctorId(@Param("doctorId") UUID doctorId);
    
    @Query("SELECT p FROM Prescription p WHERE p.appointment.id = :appointmentId")
    Prescription findByAppointmentId(@Param("appointmentId") UUID appointmentId);
}
