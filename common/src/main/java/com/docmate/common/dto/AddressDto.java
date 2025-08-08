package com.docmate.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AddressDto(
    UUID id,
    String street,
    String city,
    String state,
    String country,
    String zipCode,
    String addressType,
    Boolean isDefault
) {}
