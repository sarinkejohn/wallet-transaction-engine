package com.sarinkejohn.minipaymentengine.entity;

import com.sarinkejohn.minipaymentengine.enums.TransactionStatus;
import com.sarinkejohn.minipaymentengine.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_idempotency_key", columnList = "idempotency_key", unique = true),
        @Index(name = "idx_customer_id", columnList = "customer_id")
})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType = TransactionType.PAYMENT;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal charge = BigDecimal.ZERO;

    @Column(name = "total_debit", nullable = false)
    private BigDecimal totalDebit;

    @Column(nullable = false)
    private String currency = "TSH";

    @Column(nullable = false)
    private String channel;

    @Column(name = "receiver_mobile")
    private String receiverMobile;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (transactionType == null) {
            transactionType = TransactionType.PAYMENT;
        }
        if (currency == null) {
            currency = "TSH";
        }
    }
}
