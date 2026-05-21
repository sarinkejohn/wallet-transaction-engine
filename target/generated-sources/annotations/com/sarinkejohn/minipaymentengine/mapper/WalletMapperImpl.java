package com.sarinkejohn.minipaymentengine.mapper;

import com.sarinkejohn.minipaymentengine.dto.WalletDto;
import com.sarinkejohn.minipaymentengine.entity.Customer;
import com.sarinkejohn.minipaymentengine.entity.Wallet;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-21T17:43:52+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11-ea (Ubuntu)"
)
@Component
public class WalletMapperImpl implements WalletMapper {

    @Override
    public WalletDto toDto(Wallet wallet) {
        if ( wallet == null ) {
            return null;
        }

        WalletDto.WalletDtoBuilder walletDto = WalletDto.builder();

        walletDto.customerId( walletCustomerId( wallet ) );
        walletDto.id( wallet.getId() );
        walletDto.balance( wallet.getBalance() );

        return walletDto.build();
    }

    private Long walletCustomerId(Wallet wallet) {
        Customer customer = wallet.getCustomer();
        if ( customer == null ) {
            return null;
        }
        return customer.getId();
    }
}
