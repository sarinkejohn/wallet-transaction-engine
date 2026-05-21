package com.sarinkejohn.minipaymentengine.mapper;

import com.sarinkejohn.minipaymentengine.dto.WalletDto;
import com.sarinkejohn.minipaymentengine.entity.Customer;
import com.sarinkejohn.minipaymentengine.entity.Wallet;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-21T13:03:13+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
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
        walletDto.balance( wallet.getBalance() );
        walletDto.id( wallet.getId() );

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
