package com.docmate.payment.service;

import com.docmate.common.entity.Payment;
import com.docmate.common.enums.PaymentStatus;
import com.docmate.common.enums.PaymentMethod;
import com.docmate.common.exception.BusinessException;
import com.docmate.payment.dto.CreatePaymentRequest;
import com.docmate.payment.dto.PaymentDto;
import com.docmate.payment.mapper.PaymentMapper;
import com.docmate.payment.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.stripe.net.ApiResource.GSON;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public PaymentDto createPaymentIntent(CreatePaymentRequest request, UUID userId) {
        log.info("Creating payment intent for appointment: {} by user: {}", request.getAppointmentId(), userId);

        // Check if payment already exists for this appointment
        if (paymentRepository.findByAppointmentId(request.getAppointmentId()).isPresent()) {
            throw new BusinessException("PAYMENT_ALREADY_EXISTS", "Payment already exists for this appointment", 409);
        }

        try {
            // Create Stripe Payment Intent
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (request.getAmount().doubleValue() * 100)) // Convert to cents
                    .setCurrency("usd")
                    .setPaymentMethod(request.getPaymentMethodId())
                    .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                    .setConfirm(true)
                    .setReturnUrl("https://docmate.com/payment/return")
                    .putMetadata("appointmentId", request.getAppointmentId().toString())
                    .putMetadata("userId", userId.toString())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Create payment record
            //dummy
            Payment payment = Payment.builder()
                    .amount(request.getAmount())
                    .status(mapStripeStatusToPaymentStatus(paymentIntent.getStatus()))
                    .paymentMethod(String.valueOf(PaymentMethod.STRIPE))
                    .stripePaymentIntentId(paymentIntent.getId())
                    .build();

            payment = paymentRepository.save(payment);

            log.info("Payment intent created successfully with ID: {}", payment.getId());
            return paymentMapper.toDto(payment);

        } catch (StripeException e) {
            log.error("Stripe error creating payment intent: {}", e.getMessage());
            throw new BusinessException("PAYMENT_CREATION_FAILED", "Failed to create payment: " + e.getMessage(), 500);
        }
    }

    public PaymentDto confirmPayment(String stripePaymentIntentId) {
        log.info("Confirming payment with Stripe Payment Intent ID: {}", stripePaymentIntentId);

        Payment payment = paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
                .orElseThrow(() -> new BusinessException("PAYMENT_NOT_FOUND", "Payment not found", 404));

        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(stripePaymentIntentId);

            if ("requires_confirmation".equals(paymentIntent.getStatus())) {
                paymentIntent = paymentIntent.confirm();
            }

            payment.setStatus(mapStripeStatusToPaymentStatus(paymentIntent.getStatus()));
            payment.setTransactionId(paymentIntent.getId());

            if ("succeeded".equals(paymentIntent.getStatus())) {
                payment.setStatus(PaymentStatus.COMPLETED);
            }

            payment = paymentRepository.save(payment);

            log.info("Payment confirmed successfully with ID: {}", payment.getId());
            return paymentMapper.toDto(payment);

        } catch (StripeException e) {
            log.error("Stripe error confirming payment: {}", e.getMessage());
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new BusinessException("PAYMENT_CONFIRMATION_FAILED", "Failed to confirm payment: " + e.getMessage(), 500);
        }
    }

    public PaymentDto processRefund(UUID paymentId, BigDecimal refundAmount) {
        log.info("Processing refund for payment: {} amount: {}", paymentId, refundAmount);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException("PAYMENT_NOT_FOUND", "Payment not found", 404));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BusinessException("INVALID_PAYMENT_STATUS", "Only completed payments can be refunded", 400);
        }

        try {
            // Create refund in Stripe
            Map<String, Object> refundParams = new HashMap<>();
            refundParams.put("payment_intent", payment.getStripePaymentIntentId());
            refundParams.put("amount", (long) (refundAmount.doubleValue() * 100)); // Convert to cents

            com.stripe.model.Refund refund = com.stripe.model.Refund.create(refundParams);

            payment.setStatus(PaymentStatus.REFUNDED);
            payment = paymentRepository.save(payment);

            log.info("Refund processed successfully for payment: {}", paymentId);
            return paymentMapper.toDto(payment);

        } catch (StripeException e) {
            log.error("Stripe error processing refund: {}", e.getMessage());
            throw new BusinessException("REFUND_FAILED", "Failed to process refund: " + e.getMessage(), 500);
        }
    }

    public void handleStripeWebhook(String payload, String sigHeader) {
        log.info("Processing Stripe webhook");

        try {
            com.stripe.model.Event event = GSON.fromJson(payload, com.stripe.model.Event.class);

            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentFailed(event);
                    break;
                case "charge.dispute.created":
                    handleChargeDispute(event);
                    break;
                default:
                    log.info("Unhandled webhook event type: {}", event.getType());
            }

        } catch (Exception e) {
            log.error("Error processing Stripe webhook: {}", e.getMessage());
            throw new BusinessException("WEBHOOK_PROCESSING_FAILED", "Failed to process webhook", 500);
        }
    }

    @Transactional(readOnly = true)
    public PaymentDto getPayment(UUID paymentId) {
        log.info("Fetching payment with ID: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException("PAYMENT_NOT_FOUND", "Payment not found", 404));

        return paymentMapper.toDto(payment);
    }

    @Transactional(readOnly = true)
    public PaymentDto getPaymentByAppointment(UUID appointmentId) {
        log.info("Fetching payment for appointment: {}", appointmentId);

        Payment payment = paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new BusinessException("PAYMENT_NOT_FOUND", "Payment not found for appointment", 404));

        return paymentMapper.toDto(payment);
    }

    @Transactional(readOnly = true)
    public Page<PaymentDto> getPatientPayments(UUID patientId, Pageable pageable) {
        log.info("Fetching payments for patient: {}", patientId);

        Page<Payment> payments = paymentRepository.findByPatientId(patientId, pageable);
        return payments.map(paymentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Double getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Calculating total revenue between {} and {}", startDate, endDate);

        Double revenue = paymentRepository.getTotalRevenueBetween(startDate, endDate);
        return revenue != null ? revenue : 0.0;
    }

    private PaymentStatus mapStripeStatusToPaymentStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "succeeded" -> PaymentStatus.COMPLETED;
            case "requires_payment_method", "requires_confirmation", "requires_action", "processing" -> PaymentStatus.PENDING;
            case "canceled", "payment_failed" -> PaymentStatus.FAILED;
            default -> PaymentStatus.PENDING;
        };
    }

    private void handlePaymentSucceeded(com.stripe.model.Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
        if (paymentIntent != null) {
            paymentRepository.findByStripePaymentIntentId(paymentIntent.getId())
                    .ifPresent(payment -> {
                        payment.setStatus(PaymentStatus.COMPLETED);
                        payment.setTransactionId(paymentIntent.getId());
                        paymentRepository.save(payment);
                        log.info("Payment marked as completed via webhook: {}", payment.getId());
                    });
        }
    }

    private void handlePaymentFailed(com.stripe.model.Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
        if (paymentIntent != null) {
            paymentRepository.findByStripePaymentIntentId(paymentIntent.getId())
                    .ifPresent(payment -> {
                        payment.setStatus(PaymentStatus.FAILED);
                        paymentRepository.save(payment);
                        log.info("Payment marked as failed via webhook: {}", payment.getId());
                    });
        }
    }

    private void handleChargeDispute(com.stripe.model.Event event) {
        log.warn("Charge dispute received - manual review required");
        // Handle dispute logic here
    }
}
