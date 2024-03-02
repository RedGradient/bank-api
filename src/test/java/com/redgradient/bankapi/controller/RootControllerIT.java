package com.redgradient.bankapi.controller;

import com.redgradient.bankapi.TestUtils;
import com.redgradient.bankapi.dto.MoneyTransferDto;
import com.redgradient.bankapi.model.BankAccount;
import com.redgradient.bankapi.model.User;
import com.redgradient.bankapi.repository.BankAccountRepository;
import com.redgradient.bankapi.repository.UserRepository;
import com.redgradient.bankapi.service.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

@Transactional
@SpringBootTest
//@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor
public class RootControllerIT {
    private static final String AUTHORIZATION = "AUTHORIZATION";
    private final String ROOT_CONTROLLER = "/api";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void testTransferMoneySuccessful() throws Exception {
        var senderStartBalance = new BigDecimal("1500.00");
        var recipientStartBalance = new BigDecimal("3200.50");
        var bankAccount1 = utils.createBankAccountWithBalance(senderStartBalance);
        var bankAccount2 = utils.createBankAccountWithBalance(recipientStartBalance);
        var amount = new BigDecimal("1090.00");
        var transferDto = new MoneyTransferDto(
                bankAccount1.getId(),
                bankAccount2.getId(),
                amount
        );
        var request = post(ROOT_CONTROLLER + "/transfer")
                .content(TestUtils.asJson(transferDto))
                .contentType(MediaType.APPLICATION_JSON);
        perform(request, bankAccount1.getUser())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var expectedSenderBalance = senderStartBalance.subtract(amount);
        var expectedRecipientBalance = recipientStartBalance.add(amount);
        var actualSenderBalance = bankAccountRepository.findById(bankAccount1.getId()).orElseThrow().getBalance();
        var actualRecipientBalance = bankAccountRepository.findById(bankAccount2.getId()).orElseThrow().getBalance();

        assertEquals(expectedSenderBalance, actualSenderBalance, "Sender has wrong balance");
        assertEquals(expectedRecipientBalance, actualRecipientBalance, "Recipient has wrong balance");
    }

    @Test
    void testTransferFailsIfNotEnoughMoney() throws Exception {
        var senderStartBalance = new BigDecimal("1000.00");
        var recipientStartBalance = new BigDecimal("3200.50");
        var bankAccount1 = utils.createBankAccountWithBalance(senderStartBalance);
        var bankAccount2 = utils.createBankAccountWithBalance(recipientStartBalance);
        var amount = new BigDecimal("1200.00");
        var transferDto = new MoneyTransferDto(
                bankAccount1.getId(),
                bankAccount2.getId(),
                amount
        );
        var request = post(ROOT_CONTROLLER + "/transfer")
                .content(TestUtils.asJson(transferDto))
                .contentType(MediaType.APPLICATION_JSON);
        perform(request, bankAccount1.getUser())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        var actualSenderBalance = bankAccountRepository.findById(bankAccount1.getId()).orElseThrow().getBalance();
        var actualRecipientBalance = bankAccountRepository.findById(bankAccount2.getId()).orElseThrow().getBalance();
        assertEquals(senderStartBalance, actualSenderBalance, "Sender's balance should not change");
        assertEquals(recipientStartBalance, actualRecipientBalance, "Recipient's balance should not change");
    }

    @Test
    void testTransferFailsIfUnauthorized() throws Exception {
        var bankAccount1 = utils.createBankAccountWithBalance(new BigDecimal("2000.00"));
        var bankAccount2 = utils.createBankAccountWithBalance(new BigDecimal("3200.50"));
        var transferDto = new MoneyTransferDto(
                bankAccount1.getId(),
                bankAccount2.getId(),
                new BigDecimal("1200.00")
        );
        var request = post(ROOT_CONTROLLER + "/transfer")
                .content(TestUtils.asJson(transferDto))
                .contentType(MediaType.APPLICATION_JSON);
        perform(request)
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    void testTransferFailsIfPerformerIsNotSender() throws Exception {
        var bankAccount1 = utils.createBankAccountWithBalance(new BigDecimal("2000.00"));
        var bankAccount2 = utils.createBankAccountWithBalance(new BigDecimal("3200.50"));
        var transferDto = new MoneyTransferDto(
                bankAccount1.getId(),
                bankAccount2.getId(),
                new BigDecimal("1200.00")
        );
        var request = post(ROOT_CONTROLLER + "/transfer")
                .content(TestUtils.asJson(transferDto))
                .contentType(MediaType.APPLICATION_JSON);
        perform(request, bankAccount2.getUser())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }



    public ResultActions perform(final MockHttpServletRequestBuilder request, User user) throws Exception {
        final String token = jwtService.generateToken(user);
        request.header(AUTHORIZATION, "Bearer " + token);
        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }
}

