package com.sarinkejohn.minipaymentengine;

import com.sarinkejohn.minipaymentengine.dto.ChargeRuleRequest;
import com.sarinkejohn.minipaymentengine.dto.CustomerRequest;
import com.sarinkejohn.minipaymentengine.dto.TransactionRequest;
import com.sarinkejohn.minipaymentengine.dto.TransactionResponse;
import com.sarinkejohn.minipaymentengine.entity.Customer;
import com.sarinkejohn.minipaymentengine.entity.Wallet;
import com.sarinkejohn.minipaymentengine.enums.ChargeType;
import com.sarinkejohn.minipaymentengine.enums.Status;
import com.sarinkejohn.minipaymentengine.enums.TransactionStatus;
import com.sarinkejohn.minipaymentengine.repository.ChargeRuleRepository;
import com.sarinkejohn.minipaymentengine.repository.CustomerRepository;
import com.sarinkejohn.minipaymentengine.repository.TransactionRepository;
import com.sarinkejohn.minipaymentengine.repository.WalletRepository;
import com.sarinkejohn.minipaymentengine.service.ChargeRuleService;
import com.sarinkejohn.minipaymentengine.service.CustomerService;
import com.sarinkejohn.minipaymentengine.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MinipaymentEngineApplicationTests {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ChargeRuleRepository chargeRuleRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ChargeRuleService chargeRuleService;

    private Long activeCustomerId;
    private Long blockedCustomerId;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder().defaultStatusHandler(status -> true, (req, res) -> {
        }).build();
        transactionRepository.deleteAll();
        chargeRuleRepository.deleteAll();
        walletRepository.deleteAll();
        customerRepository.deleteAll();

        // Setup Active Customer
        CustomerRequest activeCustomerReq = new CustomerRequest("Active User", "+255711111111",
                new BigDecimal("1000.00"));
        activeCustomerId = customerService.createCustomer(activeCustomerReq).getId();

        // Setup Blocked Customer
        CustomerRequest blockedCustomerReq = new CustomerRequest("Blocked User", "+255722222222",
                new BigDecimal("500.00"));
        blockedCustomerId = customerService.createCustomer(blockedCustomerReq).getId();
        Customer blockedCustomer = customerRepository.findById(blockedCustomerId).get();
        blockedCustomer.setStatus(Status.BLOCKED);
        customerRepository.save(blockedCustomer);

        // Setup Charge Rules
        chargeRuleService.createChargeRule(new ChargeRuleRequest("USSD", BigDecimal.ZERO, new BigDecimal("100.00"),
                ChargeType.FIXED, new BigDecimal("5.00")));
        chargeRuleService.createChargeRule(new ChargeRuleRequest("USSD", new BigDecimal("100.01"),
                new BigDecimal("1000.00"), ChargeType.PERCENTAGE, new BigDecimal("2.00")));
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testSuccessfulTransaction() {
        String baseUrl = "http://localhost:" + port + "/api/transactions";
        String idempotencyKey = UUID.randomUUID().toString();

        TransactionRequest request = new TransactionRequest(
                activeCustomerId,
                new BigDecimal("50.00"),
                "TSH",
                "USSD",
                "+255733333333",
                idempotencyKey);

        ResponseEntity<TransactionResponse> response = restClient.post().uri(baseUrl).body(request).retrieve()
                .toEntity(TransactionResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TransactionStatus.SUCCESS, response.getBody().getStatus());
        assertEquals(new BigDecimal("50.00"), response.getBody().getAmount());
        assertEquals(new BigDecimal("5.00"), response.getBody().getCharge()); // Fixed charge for <= 100
        assertEquals(new BigDecimal("55.00"), response.getBody().getTotalDebit());

        // Verify wallet balance deduction
        Wallet wallet = walletRepository.findByCustomerId(activeCustomerId).get();
        assertEquals(new BigDecimal("945.00").setScale(2, RoundingMode.HALF_UP),
                wallet.getBalance().setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testInsufficientBalance() {
        String baseUrl = "http://localhost:" + port + "/api/transactions";
        String idempotencyKey = UUID.randomUUID().toString();

        // Balance is 1000. Request 1000 + 2% charge (20) = 1020 total debit > 1000
        TransactionRequest request = new TransactionRequest(
                activeCustomerId,
                new BigDecimal("1000.00"),
                "TSH",
                "USSD",
                "+255733333333",
                idempotencyKey);

        ResponseEntity<String> response = restClient.post().uri(baseUrl).body(request).retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Insufficient wallet balance"));
    }

    @Test
    void testDuplicateIdempotencyKey() {
        String baseUrl = "http://localhost:" + port + "/api/transactions";
        String idempotencyKey = UUID.randomUUID().toString();

        TransactionRequest request = new TransactionRequest(
                activeCustomerId,
                new BigDecimal("50.00"),
                "TSH",
                "USSD",
                "+255733333333",
                idempotencyKey);

        // First request
        ResponseEntity<TransactionResponse> response1 = restClient.post().uri(baseUrl).body(request).retrieve()
                .toEntity(TransactionResponse.class);
        assertEquals(HttpStatus.OK, response1.getStatusCode());

        // Second request with same idempotency key
        ResponseEntity<TransactionResponse> response2 = restClient.post().uri(baseUrl).body(request).retrieve()
                .toEntity(TransactionResponse.class);
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        // Assert the responses are identical (same ID)
        assertEquals(response1.getBody().getId(), response2.getBody().getId());

        // Verify only 1 transaction is created
        assertEquals(1, transactionRepository.count());
    }

    @Test
    void testBlockedCustomer() {
        String baseUrl = "http://localhost:" + port + "/api/transactions";
        String idempotencyKey = UUID.randomUUID().toString();

        TransactionRequest request = new TransactionRequest(
                blockedCustomerId,
                new BigDecimal("50.00"),
                "TSH",
                "USSD",
                "+255733333333",
                idempotencyKey);

        ResponseEntity<String> response = restClient.post().uri(baseUrl).body(request).retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().contains("BLOCKED"));
    }

    @Test
    void testMissingChargeRule() {
        String baseUrl = "http://localhost:" + port + "/api/transactions";
        String idempotencyKey = UUID.randomUUID().toString();

        // Request 5000, which has no rule
        TransactionRequest request = new TransactionRequest(
                activeCustomerId,
                new BigDecimal("5000.00"),
                "TSH",
                "USSD",
                "+255733333333",
                idempotencyKey);

        ResponseEntity<String> response = restClient.post().uri(baseUrl).body(request).retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("No charge rule found"));
    }
}
