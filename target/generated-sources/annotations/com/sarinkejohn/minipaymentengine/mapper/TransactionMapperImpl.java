package com.sarinkejohn.minipaymentengine.mapper;

import com.sarinkejohn.minipaymentengine.dto.TransactionRequest;
import com.sarinkejohn.minipaymentengine.dto.TransactionResponse;
import com.sarinkejohn.minipaymentengine.entity.Transaction;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-21T13:03:13+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class TransactionMapperImpl implements TransactionMapper {

    @Override
    public Transaction toEntity(TransactionRequest request) {
        if ( request == null ) {
            return null;
        }

        Transaction.TransactionBuilder transaction = Transaction.builder();

        transaction.amount( request.getAmount() );
        transaction.channel( request.getChannel() );
        transaction.currency( request.getCurrency() );
        transaction.customerId( request.getCustomerId() );
        transaction.idempotencyKey( request.getIdempotencyKey() );
        transaction.receiverMobile( request.getReceiverMobile() );

        return transaction.build();
    }

    @Override
    public TransactionResponse toResponse(Transaction transaction) {
        if ( transaction == null ) {
            return null;
        }

        TransactionResponse.TransactionResponseBuilder transactionResponse = TransactionResponse.builder();

        transactionResponse.amount( transaction.getAmount() );
        transactionResponse.channel( transaction.getChannel() );
        transactionResponse.charge( transaction.getCharge() );
        transactionResponse.createdAt( transaction.getCreatedAt() );
        transactionResponse.currency( transaction.getCurrency() );
        transactionResponse.customerId( transaction.getCustomerId() );
        transactionResponse.failureReason( transaction.getFailureReason() );
        transactionResponse.id( transaction.getId() );
        transactionResponse.idempotencyKey( transaction.getIdempotencyKey() );
        transactionResponse.receiverMobile( transaction.getReceiverMobile() );
        transactionResponse.status( transaction.getStatus() );
        transactionResponse.totalDebit( transaction.getTotalDebit() );
        transactionResponse.transactionType( transaction.getTransactionType() );

        return transactionResponse.build();
    }
}
