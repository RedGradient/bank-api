package com.redgradient.bankapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redgradient.bankapi.model.BankAccount;
import com.redgradient.bankapi.model.User;
import com.redgradient.bankapi.repository.BankAccountRepository;
import com.redgradient.bankapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TestUtils {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public BankAccount createBankAccountWithBalance(BigDecimal balance) {
        var email = String.format("example-%s@mail.com", userRepository.count() + 1);
        var username = String.format("username-%s", userRepository.count() + 1);
        var user = User.builder()
                .firstName("Firstname")
                .lastName("Lastname")
                .email(email)
                .username(username)
                .password("password")
                .build();
        var bankAccount = BankAccount.builder()
                .balance(balance)
                .user(user)
                .build();

        userRepository.save(user);
        return bankAccountRepository.save(bankAccount);
    }
}
