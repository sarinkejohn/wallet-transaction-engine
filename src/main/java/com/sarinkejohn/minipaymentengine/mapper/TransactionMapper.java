package com.sarinkejohn.minipaymentengine.mapper;

import com.sarinkejohn.minipaymentengine.dto.TransactionRequest;
import com.sarinkejohn.minipaymentengine.dto.TransactionResponse;
import com.sarinkejohn.minipaymentengine.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionType", ignore = true)
    @Mapping(target = "charge", ignore = true)
    @Mapping(target = "totalDebit", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "failureReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Transaction toEntity(TransactionRequest request);

    TransactionResponse toResponse(Transaction transaction);
}
