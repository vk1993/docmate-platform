package com.docmate.payment.mapper;

import com.docmate.common.entity.Payment;
import com.docmate.payment.dto.PaymentDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    PaymentDto toDto(Payment payment);

    Payment toEntity(PaymentDto paymentDto);
}
