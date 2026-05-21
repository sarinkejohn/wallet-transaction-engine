package com.sarinkejohn.minipaymentengine.dto;

import com.sarinkejohn.minipaymentengine.enums.ChargeType;
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
public class ChargeRuleRequest {

    @NotBlank(message = "Channel is required")
    private String channel;

    @NotNull(message = "Minimum amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum amount must be greater than or equal to 0")
    private BigDecimal minAmount;

    @NotNull(message = "Maximum amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Maximum amount must be greater than 0")
    private BigDecimal maxAmount;

    @NotNull(message = "Charge type is required (FIXED or PERCENTAGE)")
    private ChargeType chargeType;

    @NotNull(message = "Charge value is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Charge value must be greater than or equal to 0")
    private BigDecimal chargeValue;
}
