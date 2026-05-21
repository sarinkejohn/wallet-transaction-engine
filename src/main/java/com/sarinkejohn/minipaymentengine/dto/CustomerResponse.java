package com.sarinkejohn.minipaymentengine.dto;

import com.sarinkejohn.minipaymentengine.enums.Status;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {
    private Long id;
    private String name;
    private String mobileNumber;
    private Status status;
    private LocalDateTime createdAt;
}
