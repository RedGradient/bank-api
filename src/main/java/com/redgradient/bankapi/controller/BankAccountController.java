package com.redgradient.bankapi.controller;

import com.redgradient.bankapi.model.BankAccount;
import com.redgradient.bankapi.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping(path = "/api/bank_account")
public class BankAccountController {
    @Autowired
    private BankAccountService bankAccountService;

    @GetMapping
    public Iterable<BankAccount> getBankAccounts() {
        return bankAccountService.getBankAccounts();
    }
}
