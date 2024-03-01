package com.redgradient.bankapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Entity
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true, nullable = false)
    private User user;

    @Setter
    @Positive(message = "There are insufficient funds in the account")
    private BigDecimal balance;

    @CreatedDate
    private Date createdAt;

    @OneToOne(mappedBy = "bankAccount")
    private Deposit deposit;
}
