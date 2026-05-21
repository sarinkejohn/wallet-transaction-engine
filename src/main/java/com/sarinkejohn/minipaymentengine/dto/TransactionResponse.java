package com.sarinkejohn.minipaymentengine.dto;

import com.sarinkejohn.minipaymentengine.enums.TransactionStatus;
import com.sarinkejohn.minipaymentengine.enums.TransactionType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private Long id;
    private Long customerId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal charge;
    private BigDecimal totalDebit;
    private String currency;
    private String channel;
    private String receiverMobile;
    private String idempotencyKey;
    private TransactionStatus status;
    private String failureReason;
    private LocalDateTime createdAt;
}
