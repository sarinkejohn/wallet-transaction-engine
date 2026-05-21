package com.sarinkejohn.minipaymentengine.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletDto {
    private Long id;
    private Long customerId;
    private BigDecimal balance;
}
