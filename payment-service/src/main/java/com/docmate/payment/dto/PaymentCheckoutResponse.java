package com.docmate.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCheckoutResponse {
    private Long paymentId;
    private String clientSecret;
    private BigDecimal amount;
    private String currency;
}
