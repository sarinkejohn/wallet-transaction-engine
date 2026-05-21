package com.sarinkejohn.minipaymentengine.dto;

import com.sarinkejohn.minipaymentengine.enums.ChargeType;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargeRuleResponse {
    private Long id;
    private String channel;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private ChargeType chargeType;
    private BigDecimal chargeValue;
}
