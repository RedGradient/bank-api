package com.redgradient.bankapi.controller;

import com.redgradient.bankapi.dto.MoneyTransferRequest;
import com.redgradient.bankapi.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Controller
@RequestMapping(path = "/api")
public class RootController {
    @Autowired
    private BankAccountService bankAccountService;

    @PostMapping(path = "/transfer")
    public ResponseEntity<String> transferMoney(@RequestBody MoneyTransferRequest request) {
        bankAccountService.transferMoney(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount()
        );
        return ResponseEntity.ok("Money transferred successfully");
    }
}
