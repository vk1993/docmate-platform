package com.docmate.user.mapper;

import com.docmate.common.dto.AddressDto;
import com.docmate.common.entity.Address;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-08T21:59:52+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class AddressMapperImpl implements AddressMapper {

    @Override
    public AddressDto toDto(Address address) {
        if ( address == null ) {
            return null;
        }

        UUID id = null;
        String city = null;
        String state = null;
        String country = null;
        String zipCode = null;

        id = address.getId();
        city = address.getCity();
        state = address.getState();
        country = address.getCountry();
        zipCode = address.getZipCode();

        String street = null;
        String addressType = null;
        Boolean isDefault = null;

        AddressDto addressDto = new AddressDto( id, street, city, state, country, zipCode, addressType, isDefault );

        return addressDto;
    }

    @Override
    public Address toEntity(AddressDto addressDto) {
        if ( addressDto == null ) {
            return null;
        }

        Address.AddressBuilder address = Address.builder();

        address.city( addressDto.city() );
        address.state( addressDto.state() );
        address.zipCode( addressDto.zipCode() );
        address.country( addressDto.country() );

        return address.build();
    }
}
