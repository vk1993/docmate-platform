package com.docmate.common.entity;

import com.docmate.common.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private CommonAppointment appointment;

    @NotNull(message = "Amount is required")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Size(max = 50, message = "Payment method must not exceed 50 characters")
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @Size(max = 200, message = "Transaction ID must not exceed 200 characters")
    @Column(name = "transaction_id", length = 200)
    private String transactionId;
    
    @Size(max = 200, message = "Stripe payment intent ID must not exceed 200 characters")
    @Column(name = "stripe_payment_intent_id", length = 200)
    private String stripePaymentIntentId;
}
