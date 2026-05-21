package com.sarinkejohn.minipaymentengine.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required (e.g. KES)")
    private String currency = "KES";

    @NotBlank(message = "Channel is required (e.g. MOBILE_APP, USSD, WEB)")
    private String channel;

    private String receiverMobile;

    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;
}
