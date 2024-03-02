package com.redgradient.bankapi.controller;

import com.redgradient.bankapi.dto.MoneyTransferDto;
import com.redgradient.bankapi.exception.InsufficientFundsException;
import com.redgradient.bankapi.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/api")
public class RootController {
    @Autowired
    private BankAccountService bankAccountService;
    private static final String AUTHENTICATED = "isAuthenticated()";
    private static final String ONLY_SENDER = """
        @userRepository.findById(#request.fromAccountId).get().getUsername() == authentication.name
        """;

    @PreAuthorize(AUTHENTICATED + " and " + ONLY_SENDER)
    @PostMapping(path = "/transfer")
    public ResponseEntity<String> transferMoney(@RequestBody MoneyTransferDto request) {
        try {
            bankAccountService.transferMoney(
                    request.getFromAccountId(),
                    request.getToAccountId(),
                    request.getAmount()
            );
        } catch (InsufficientFundsException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return ResponseEntity.ok("Money transferred successfully");
    }
}
