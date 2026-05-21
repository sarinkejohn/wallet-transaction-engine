package com.sarinkejohn.minipaymentengine.mapper;

import com.sarinkejohn.minipaymentengine.dto.CustomerRequest;
import com.sarinkejohn.minipaymentengine.dto.CustomerResponse;
import com.sarinkejohn.minipaymentengine.entity.Customer;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-21T17:43:52+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11-ea (Ubuntu)"
)
@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public Customer toEntity(CustomerRequest request) {
        if ( request == null ) {
            return null;
        }

        Customer.CustomerBuilder customer = Customer.builder();

        customer.name( request.getName() );
        customer.mobileNumber( request.getMobileNumber() );

        return customer.build();
    }

    @Override
    public CustomerResponse toResponse(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        CustomerResponse.CustomerResponseBuilder customerResponse = CustomerResponse.builder();

        customerResponse.id( customer.getId() );
        customerResponse.name( customer.getName() );
        customerResponse.mobileNumber( customer.getMobileNumber() );
        customerResponse.status( customer.getStatus() );
        customerResponse.createdAt( customer.getCreatedAt() );

        return customerResponse.build();
    }
}
