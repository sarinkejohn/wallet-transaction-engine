package com.sarinkejohn.minipaymentengine.mapper;

import com.sarinkejohn.minipaymentengine.dto.WalletDto;
import com.sarinkejohn.minipaymentengine.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @Mapping(source = "customer.id", target = "customerId")
    WalletDto toDto(Wallet wallet);
}
