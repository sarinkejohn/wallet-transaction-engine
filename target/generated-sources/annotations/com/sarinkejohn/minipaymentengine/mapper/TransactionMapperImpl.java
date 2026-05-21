package com.sarinkejohn.minipaymentengine.mapper;

import com.sarinkejohn.minipaymentengine.dto.TransactionRequest;
import com.sarinkejohn.minipaymentengine.dto.TransactionResponse;
import com.sarinkejohn.minipaymentengine.entity.Transaction;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-21T17:43:52+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11-ea (Ubuntu)"
)
@Component
public class TransactionMapperImpl implements TransactionMapper {

    @Override
    public Transaction toEntity(TransactionRequest request) {
        if ( request == null ) {
            return null;
        }

        Transaction.TransactionBuilder transaction = Transaction.builder();

        transaction.customerId( request.getCustomerId() );
        transaction.amount( request.getAmount() );
        transaction.currency( request.getCurrency() );
        transaction.channel( request.getChannel() );
        transaction.receiverMobile( request.getReceiverMobile() );
        transaction.idempotencyKey( request.getIdempotencyKey() );

        return transaction.build();
    }

    @Override
    public TransactionResponse toResponse(Transaction transaction) {
        if ( transaction == null ) {
            return null;
        }

        TransactionResponse.TransactionResponseBuilder transactionResponse = TransactionResponse.builder();

        transactionResponse.id( transaction.getId() );
        transactionResponse.customerId( transaction.getCustomerId() );
        transactionResponse.transactionType( transaction.getTransactionType() );
        transactionResponse.amount( transaction.getAmount() );
        transactionResponse.charge( transaction.getCharge() );
        transactionResponse.totalDebit( transaction.getTotalDebit() );
        transactionResponse.currency( transaction.getCurrency() );
        transactionResponse.channel( transaction.getChannel() );
        transactionResponse.receiverMobile( transaction.getReceiverMobile() );
        transactionResponse.idempotencyKey( transaction.getIdempotencyKey() );
        transactionResponse.status( transaction.getStatus() );
        transactionResponse.failureReason( transaction.getFailureReason() );
        transactionResponse.createdAt( transaction.getCreatedAt() );

        return transactionResponse.build();
    }
}
