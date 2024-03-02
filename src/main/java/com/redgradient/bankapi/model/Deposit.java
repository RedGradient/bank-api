package com.redgradient.bankapi.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Entity
public class Deposit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private BigDecimal initialAmount;
    @Column(nullable = false)
    private BigDecimal increasePercentage;
    @Column(nullable = false)
    private BigDecimal targetPercentage;

    @OneToOne
    @JoinColumn(name = "bank_account_id", referencedColumnName = "id", unique = true, nullable = false)
    private BankAccount bankAccount;
}
