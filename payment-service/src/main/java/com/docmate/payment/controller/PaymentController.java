package com.docmate.payment.controller;

import com.docmate.common.dto.response.ApiResponse;
import com.docmate.common.dto.response.PageResponse;
import com.docmate.common.entity.User;
import com.docmate.payment.dto.CreatePaymentRequest;
import com.docmate.payment.dto.PaymentDto;
import com.docmate.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "Payment processing and management APIs")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/create-intent")
    @Operation(summary = "Create payment intent", description = "Create a payment intent for appointment payment")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<PaymentDto>> createPaymentIntent(
            @Valid @RequestBody CreatePaymentRequest request,
            @AuthenticationPrincipal User currentUser) {
        PaymentDto payment = paymentService.createPaymentIntent(request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Payment intent created successfully", payment));
    }
    
    @PostMapping("/confirm/{stripePaymentIntentId}")
    @Operation(summary = "Confirm payment", description = "Confirm payment using Stripe Payment Intent ID")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<PaymentDto>> confirmPayment(@PathVariable String stripePaymentIntentId) {
        PaymentDto payment = paymentService.confirmPayment(stripePaymentIntentId);
        return ResponseEntity.ok(ApiResponse.success("Payment confirmed successfully", payment));
    }
    
    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Process refund", description = "Process refund for a payment (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentDto>> processRefund(
            @PathVariable UUID paymentId,
            @RequestParam BigDecimal refundAmount) {
        PaymentDto payment = paymentService.processRefund(paymentId, refundAmount);
        return ResponseEntity.ok(ApiResponse.success("Refund processed successfully", payment));
    }
    
    @PostMapping("/webhook")
    @Operation(summary = "Stripe webhook", description = "Handle Stripe webhook events")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        paymentService.handleStripeWebhook(payload, sigHeader);
        return ResponseEntity.ok("Webhook processed");
    }
    
    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment details", description = "Get payment details by ID")
    public ResponseEntity<ApiResponse<PaymentDto>> getPayment(@PathVariable UUID paymentId) {
        PaymentDto payment = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }
    
    @GetMapping("/appointment/{appointmentId}")
    @Operation(summary = "Get payment by appointment", description = "Get payment details by appointment ID")
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentByAppointment(@PathVariable UUID appointmentId) {
        PaymentDto payment = paymentService.getPaymentByAppointment(appointmentId);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }
    
    @GetMapping("/patient/my")
    @Operation(summary = "Get patient payments", description = "Get current patient's payment history")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<PageResponse<PaymentDto>>> getMyPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentDto> payments = paymentService.getPatientPayments(currentUser.getId(), pageable);
        PageResponse<PaymentDto> pageResponse = PageResponse.of(
                payments.getContent(), 
                payments.getNumber(), 
                payments.getSize(), 
                payments.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }
    
    @GetMapping("/revenue")
    @Operation(summary = "Get revenue report", description = "Get total revenue between dates (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Double>> getRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Double revenue = paymentService.getTotalRevenue(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Revenue calculated successfully", revenue));
    }
}
