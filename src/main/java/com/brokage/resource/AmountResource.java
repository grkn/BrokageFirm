package com.brokage.resource;

import com.brokage.constant.Currency;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmountResource {
    private Currency currency;
    private BigDecimal amount;
}
