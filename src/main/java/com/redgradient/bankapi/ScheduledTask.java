package com.redgradient.bankapi;

import com.redgradient.bankapi.model.BankAccount;
import com.redgradient.bankapi.model.Deposit;
import com.redgradient.bankapi.repository.BankAccountRepository;
import com.redgradient.bankapi.repository.DepositRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ScheduledTask {
    private final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);
    private final BankAccountRepository bankAccountRepository;
    private final DepositRepository depositRepository;

    public ScheduledTask(BankAccountRepository bankAccountRepository, DepositRepository depositRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.depositRepository = depositRepository;
    }

    @Scheduled(fixedDelay = 60000)
    public void myTask() {
        depositRepository.findAll().forEach(this::payInterest);
    }

    @Transactional
    private void payInterest(Deposit deposit) {
        var bankAccount = deposit.getBankAccount();
        var targetBalance = calculateTargetBalance(deposit);
        var balance = calculateNewBalance(deposit);

        if (balance.compareTo(targetBalance) >= 0) {
            updateAndSaveBalance(bankAccount, targetBalance);
            logInterestPayment(deposit);
            closeDeposit(deposit);
            return;
        }
        logInterestPayment(deposit);
        updateAndSaveBalance(bankAccount, balance);
    }

    private void logInterestPayment(Deposit deposit) {
        logger.info("PAY INTEREST of {}% to BankAccount ID {}; NEW BALANCE: {}",
                deposit.getIncreasePercentage(),
                deposit.getBankAccount().getId(),
                deposit.getBankAccount().getBalance());
    }

    private void closeDeposit(Deposit deposit) {
        var bankAccount = deposit.getBankAccount();
        depositRepository.delete(deposit);
        logger.info("DEPOSIT IS CLOSED for BankAccount ID {}", bankAccount.getId());
    }

    private void updateAndSaveBalance(BankAccount bankAccount, BigDecimal balance) {
        bankAccount.setBalance(balance);
        bankAccountRepository.save(bankAccount);
    }

    private BigDecimal calculateTargetBalance(Deposit deposit) {
        var increaseMultiplier = deposit.getTargetPercentage().divide(new BigDecimal("100"), RoundingMode.HALF_UP);
        return deposit.getInitialAmount().multiply(increaseMultiplier);
    }

    private BigDecimal calculateNewBalance(Deposit deposit) {
        var increaseMultiplier = BigDecimal.ONE.add(deposit.getIncreasePercentage().divide(new BigDecimal("100"), RoundingMode.HALF_UP));
        return deposit.getBankAccount().getBalance().multiply(increaseMultiplier);
    }
}
