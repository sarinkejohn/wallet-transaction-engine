package com.sarinkejohn.minipaymentengine.controller;

import com.sarinkejohn.minipaymentengine.dto.CustomerRequest;
import com.sarinkejohn.minipaymentengine.dto.CustomerResponse;
import com.sarinkejohn.minipaymentengine.dto.WalletDto;
import com.sarinkejohn.minipaymentengine.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "APIs for managing customers and wallets")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create a new customer")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a customer by ID")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/{id}/wallet")
    @Operation(summary = "Get a customer's wallet by customer ID")
    public ResponseEntity<WalletDto> getCustomerWallet(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerWallet(id));
    }
}
