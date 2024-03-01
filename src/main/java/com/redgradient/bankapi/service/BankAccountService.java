package com.redgradient.bankapi.service;

import com.redgradient.bankapi.model.BankAccount;
import com.redgradient.bankapi.repository.BankAccountRepository;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public Iterable<BankAccount> getBankAccounts() {
        return bankAccountRepository.findAll();
    }

    @Transactional
    public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        try {
            BankAccount fromAccount = bankAccountRepository.findByIdWithLock(fromAccountId)
                    .orElseThrow(() -> new RuntimeException("Счет " + fromAccountId + " не найден"));

            BankAccount toAccount = bankAccountRepository.findByIdWithLock(toAccountId)
                    .orElseThrow(() -> new RuntimeException("Счет " + toAccountId + " не найден"));

            var comparisonResult = fromAccount.getBalance().compareTo(amount);
            if (comparisonResult < 0) {
                throw new RuntimeException("Недостаточно средств на счете " + fromAccountId);
            }

            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            toAccount.setBalance(toAccount.getBalance().add(amount));

            bankAccountRepository.save(fromAccount);
            bankAccountRepository.save(toAccount);
        } catch (PessimisticLockException | LockTimeoutException ex) {
            throw new RuntimeException("Не удалось получить блокировку на счетах", ex);
        }
    }
}
