package com.redgradient.bankapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MoneyTransferDto {
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
}
