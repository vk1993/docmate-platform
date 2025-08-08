package com.docmate.payment.repository;

import com.docmate.common.entity.Payment;
import com.docmate.common.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByAppointmentId(UUID appointmentId);

    @Query("SELECT p FROM Payment p WHERE p.appointment.patient.id = :patientId")
    List<Payment> findByPatientId(@Param("patientId") UUID patientId);

    @Query("SELECT p FROM Payment p WHERE p.appointment.patient.id = :patientId")
    Page<Payment> findByPatientId(@Param("patientId") UUID patientId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.appointment.doctor.id = :doctorId")
    List<Payment> findByDoctorId(@Param("doctorId") UUID doctorId);

    @Query("SELECT p FROM Payment p WHERE p.appointment.doctor.id = :doctorId")
    Page<Payment> findByDoctorId(@Param("doctorId") UUID doctorId, Pageable pageable);

    List<Payment> findByStatus(PaymentStatus status);

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    Optional<Payment> findByTransactionId(String transactionId);

    @Query("SELECT p FROM Payment p WHERE p.createdDate BETWEEN :startDate AND :endDate")
    List<Payment> findByPaymentDateBetween(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Payment p WHERE p.amount BETWEEN :minAmount AND :maxAmount")
    List<Payment> findByAmountBetween(@Param("minAmount") BigDecimal minAmount,
                                      @Param("maxAmount") BigDecimal maxAmount);

    @Query("SELECT p FROM Payment p WHERE p.paymentMethod = :paymentMethod")
    List<Payment> findByPaymentMethod(@Param("paymentMethod") String paymentMethod);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.appointment.doctor.id = :doctorId AND p.status = 'COMPLETED'")
    BigDecimal getTotalEarningsByDoctor(@Param("doctorId") UUID doctorId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.createdDate BETWEEN :startDate AND :endDate")
    Double getTotalRevenueBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    long countByStatus(@Param("status") PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED' AND p.createdDate >= :since")
    List<Payment> findFailedPaymentsSince(@Param("since") LocalDateTime since);
}
