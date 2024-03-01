package com.redgradient.bankapi.repository;

import com.redgradient.bankapi.model.Deposit;
import org.springframework.data.repository.CrudRepository;

public interface DepositRepository extends CrudRepository<Deposit, Long> {
}
