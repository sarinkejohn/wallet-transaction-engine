package com.sarinkejohn.minipaymentengine.service;

import com.sarinkejohn.minipaymentengine.dto.TransactionRequest;
import com.sarinkejohn.minipaymentengine.dto.TransactionResponse;
import com.sarinkejohn.minipaymentengine.entity.ChargeRule;
import com.sarinkejohn.minipaymentengine.entity.Customer;
import com.sarinkejohn.minipaymentengine.entity.Transaction;
import com.sarinkejohn.minipaymentengine.entity.Wallet;
import com.sarinkejohn.minipaymentengine.enums.ChargeType;
import com.sarinkejohn.minipaymentengine.enums.Status;
import com.sarinkejohn.minipaymentengine.enums.TransactionStatus;
import com.sarinkejohn.minipaymentengine.enums.TransactionType;
import com.sarinkejohn.minipaymentengine.exception.*;
import com.sarinkejohn.minipaymentengine.mapper.TransactionMapper;
import com.sarinkejohn.minipaymentengine.repository.ChargeRuleRepository;
import com.sarinkejohn.minipaymentengine.repository.CustomerRepository;
import com.sarinkejohn.minipaymentengine.repository.TransactionRepository;
import com.sarinkejohn.minipaymentengine.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final WalletRepository walletRepository;
    private final ChargeRuleRepository chargeRuleRepository;
    private final TransactionMapper transactionMapper;
    private final RedisLockService redisLockService;

    private static final String LOCK_PREFIX = "lock:idempotency:";
    private static final long LOCK_EXPIRE_SECONDS = 30;

    @Transactional
    public TransactionResponse processTransaction(TransactionRequest request) {
        log.info("Processing transaction for customer ID: {} with idempotency key: {}",
                request.getCustomerId(), request.getIdempotencyKey());

        // 1. Idempotency check: return early if already processed
        Optional<Transaction> existing = transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existing.isPresent()) {
            log.info("Duplicate request detected for idempotency key: {}. Returning cached response.", request.getIdempotencyKey());
            return transactionMapper.toResponse(existing.get());
        }

        // 2. Acquire distributed lock to prevent concurrent duplicate processing
        String lockKey = LOCK_PREFIX + request.getIdempotencyKey();
        boolean locked = redisLockService.acquireLock(lockKey, LOCK_EXPIRE_SECONDS);
        if (!locked) {
            throw new ConcurrentRequestException(
                    "A concurrent transaction request with the same idempotency key is being processed. Please retry shortly.");
        }

        try {
            // 3. Re-check idempotency after lock acquired (double-check pattern)
            Optional<Transaction> recheck = transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (recheck.isPresent()) {
                log.info("Duplicate request detected after lock for idempotency key: {}.", request.getIdempotencyKey());
                return transactionMapper.toResponse(recheck.get());
            }

            // 4. Validate customer
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new UserNotFoundException("Customer not found with ID: " + request.getCustomerId()));

            if (customer.getStatus() == Status.BLOCKED) {
                throw new AccountBlockedException("Customer account is BLOCKED. Transaction denied for customer ID: " + request.getCustomerId());
            }

            // 5. Find matching charge rule
            ChargeRule rule = chargeRuleRepository
                    .findByChannelIgnoreCaseAndMinAmountLessThanEqualAndMaxAmountGreaterThanEqual(
                            request.getChannel(), request.getAmount(), request.getAmount())
                    .orElseThrow(() -> new MissingChargeRuleException(
                            "No charge rule found for channel '" + request.getChannel() + "' and amount " + request.getAmount()));

            // 6. Calculate charge
            BigDecimal charge = calculateCharge(request.getAmount(), rule);
            BigDecimal totalDebit = request.getAmount().add(charge).setScale(2, RoundingMode.HALF_UP);

            // 7. Validate wallet balance
            Wallet wallet = walletRepository.findByCustomerId(customer.getId())
                    .orElseThrow(() -> new UserNotFoundException("Wallet not found for customer ID: " + customer.getId()));

            if (wallet.getBalance().compareTo(totalDebit) < 0) {
                throw new InsufficientFundsException(
                        String.format("Insufficient wallet balance. Required: %.2f, Available: %.2f",
                                totalDebit, wallet.getBalance()));
            }

            // 8. Deduct balance
            wallet.setBalance(wallet.getBalance().subtract(totalDebit));
            walletRepository.save(wallet);

            // 9. Persist transaction
            Transaction transaction = Transaction.builder()
                    .customerId(customer.getId())
                    .transactionType(TransactionType.PAYMENT)
                    .amount(request.getAmount().setScale(2, RoundingMode.HALF_UP))
                    .charge(charge)
                    .totalDebit(totalDebit)
                    .currency(request.getCurrency())
                    .channel(request.getChannel())
                    .receiverMobile(request.getReceiverMobile())
                    .idempotencyKey(request.getIdempotencyKey())
                    .status(TransactionStatus.SUCCESS)
                    .build();

            Transaction saved = transactionRepository.save(transaction);
            log.info("Transaction {} processed successfully. Amount: {}, Charge: {}, Total Debit: {}",
                    saved.getId(), saved.getAmount(), saved.getCharge(), saved.getTotalDebit());

            return transactionMapper.toResponse(saved);

        } finally {
            redisLockService.releaseLock(lockKey);
        }
    }

    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Transaction not found with ID: " + id));
        return transactionMapper.toResponse(transaction);
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll()
                .stream().map(transactionMapper::toResponse).collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByCustomer(Long customerId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new UserNotFoundException("Customer not found with ID: " + customerId));
        return transactionRepository.findByCustomerId(customerId)
                .stream().map(transactionMapper::toResponse).collect(Collectors.toList());
    }

    private BigDecimal calculateCharge(BigDecimal amount, ChargeRule rule) {
        if (rule.getChargeType() == ChargeType.FIXED) {
            return rule.getChargeValue().setScale(2, RoundingMode.HALF_UP);
        } else {
            // PERCENTAGE: e.g., chargeValue = 1.5 means 1.5%
            return amount
                    .multiply(rule.getChargeValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
    }
}
