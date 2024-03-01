package com.redgradient.bankapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redgradient.bankapi.dto.MoneyTransferDto;
import com.redgradient.bankapi.model.BankAccount;
import com.redgradient.bankapi.model.User;
import com.redgradient.bankapi.repository.BankAccountRepository;
import com.redgradient.bankapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.math.BigDecimal;

@Transactional
@SpringBootTest
@RequiredArgsConstructor
public class RootControllerTest {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
    private final String ROOT_CONTROLLER = "/api";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Test
    void testTransferMoneySuccessful() throws JsonProcessingException {
        var user1 = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("example@mail.com")
                .phoneNumber("+79993012369")
                .username("john_doe")
                .password("password")
                .build();
        var bankAccount1 = BankAccount.builder()
                .balance(new BigDecimal("1500.00"))
                .user(user1)
                .build();

        var user2 = User.builder()
                .firstName("Alice")
                .lastName("Doe")
                .email("alice@mail.com")
                .phoneNumber("+79993369435")
                .username("alice")
                .password("password")
                .build();
        var bankAccount2 = BankAccount.builder()
                .balance(new BigDecimal("3200.50"))
                .user(user2)
                .build();

        var amount = new BigDecimal("1090.00");
        var transferDto = new MoneyTransferDto(
                bankAccount1.getId(),
                bankAccount2.getId(),
                amount
        );

        userRepository.save(user1);
        userRepository.save(user2);
        bankAccountRepository.save(bankAccount1);
        bankAccountRepository.save(bankAccount2);

        post(ROOT_CONTROLLER + "/transfer")
                .content(asJson(transferDto))
                .contentType(MediaType.APPLICATION_JSON);

        var expectedBalance1 = bankAccount1.getBalance().subtract(amount);
        var expectedBalance2 = bankAccount2.getBalance().add(amount);
        var actualBalance1 = bankAccountRepository.findById(bankAccount1.getId()).orElseThrow().getBalance();
        var actualBalance2 = bankAccountRepository.findById(bankAccount2.getId()).orElseThrow().getBalance();

        assertEquals(expectedBalance1, actualBalance1);
        assertEquals(expectedBalance2, actualBalance2);
    }

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }
}
