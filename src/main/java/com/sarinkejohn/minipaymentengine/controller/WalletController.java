package com.sarinkejohn.minipaymentengine.controller;

import com.sarinkejohn.minipaymentengine.dto.WalletDto;
import com.sarinkejohn.minipaymentengine.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@Tag(name = "Wallet Management", description = "APIs for managing wallets")
public class WalletController {

    private final CustomerService customerService;

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get wallet details by customer ID")
    public ResponseEntity<WalletDto> getWalletByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerWallet(customerId));
    }
}
