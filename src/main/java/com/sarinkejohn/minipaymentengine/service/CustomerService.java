package com.sarinkejohn.minipaymentengine.service;

import com.sarinkejohn.minipaymentengine.dto.CustomerRequest;
import com.sarinkejohn.minipaymentengine.dto.CustomerResponse;
import com.sarinkejohn.minipaymentengine.dto.WalletDto;
import com.sarinkejohn.minipaymentengine.entity.Customer;
import com.sarinkejohn.minipaymentengine.entity.Wallet;
import com.sarinkejohn.minipaymentengine.enums.Status;
import com.sarinkejohn.minipaymentengine.exception.DuplicateReferenceException;
import com.sarinkejohn.minipaymentengine.exception.UserNotFoundException;
import com.sarinkejohn.minipaymentengine.mapper.CustomerMapper;
import com.sarinkejohn.minipaymentengine.mapper.WalletMapper;
import com.sarinkejohn.minipaymentengine.repository.CustomerRepository;
import com.sarinkejohn.minipaymentengine.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final WalletRepository walletRepository;
    private final CustomerMapper customerMapper;
    private final WalletMapper walletMapper;

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        log.info("Creating new customer with mobile number: {}", request.getMobileNumber());
        if (customerRepository.findByMobileNumber(request.getMobileNumber()).isPresent()) {
            throw new DuplicateReferenceException("Customer with mobile number " + request.getMobileNumber() + " already exists");
        }

        Customer customer = customerMapper.toEntity(request);
        customer.setStatus(Status.ACTIVE);
        
        Customer savedCustomer = customerRepository.save(customer);

        BigDecimal initialBal = request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO;
        Wallet wallet = Wallet.builder()
                .customer(savedCustomer)
                .balance(initialBal)
                .build();
        
        walletRepository.save(wallet);
        savedCustomer.setWallet(wallet);

        return customerMapper.toResponse(savedCustomer);
    }

    public CustomerResponse getCustomerById(Long id) {
        log.info("Retrieving customer details for ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Customer not found with ID: " + id));
        return customerMapper.toResponse(customer);
    }

    public WalletDto getCustomerWallet(Long id) {
        log.info("Retrieving wallet details for customer ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Customer not found with ID: " + id));
        
        Wallet wallet = customer.getWallet();
        if (wallet == null) {
            throw new UserNotFoundException("Wallet not found for customer ID: " + id);
        }

        return walletMapper.toDto(wallet);
    }
}
