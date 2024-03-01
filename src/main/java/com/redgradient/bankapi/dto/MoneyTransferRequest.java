package com.redgradient.bankapi.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MoneyTransferRequest {
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
}
