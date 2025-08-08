package com.docmate.payment.mapper;

import com.docmate.common.entity.Payment;
import com.docmate.payment.dto.PaymentDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-08T21:59:54+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public PaymentDto toDto(Payment payment) {
        if ( payment == null ) {
            return null;
        }

        PaymentDto.PaymentDtoBuilder paymentDto = PaymentDto.builder();

        paymentDto.id( payment.getId() );
        paymentDto.amount( payment.getAmount() );
        paymentDto.status( payment.getStatus() );
        paymentDto.paymentMethod( payment.getPaymentMethod() );
        paymentDto.transactionId( payment.getTransactionId() );
        paymentDto.stripePaymentIntentId( payment.getStripePaymentIntentId() );
        paymentDto.createdDate( payment.getCreatedDate() );
        paymentDto.updatedDate( payment.getUpdatedDate() );

        return paymentDto.build();
    }

    @Override
    public Payment toEntity(PaymentDto paymentDto) {
        if ( paymentDto == null ) {
            return null;
        }

        Payment.PaymentBuilder payment = Payment.builder();

        payment.amount( paymentDto.getAmount() );
        payment.status( paymentDto.getStatus() );
        payment.paymentMethod( paymentDto.getPaymentMethod() );
        payment.transactionId( paymentDto.getTransactionId() );
        payment.stripePaymentIntentId( paymentDto.getStripePaymentIntentId() );

        return payment.build();
    }
}
