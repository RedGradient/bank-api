package com.redgradient.bankapi.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(Long bankAccountId) {
        super("Insufficient funds in the account " + bankAccountId);
    }
}
