package com.donus.balance;

import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;

@Entity
@Table(name = "BALANCE_TRANSACTION")
public class BalanceTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @Column(name = "BALANCE", nullable = false)
    private BigDecimal balance;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "ACCOUNT", nullable = false, insertable = false, updatable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSACTION_TYPE", nullable = false)
    private TransactionType transactionType;

    //jpa requirement
    protected BalanceTransaction() {
    }

    public BalanceTransaction(final Long accountId,
                              final BigDecimal amount,
                              final BigDecimal balance,
                              final TransactionType transactionType) {
        this.accountId = requireNonNull(accountId);
        this.amount = requireNonNull(amount);
        this.balance = requireNonNull(balance);
        this.transactionType = requireNonNull(transactionType);
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = requireNonNull(amount);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(final BigDecimal balance) {
        this.balance = requireNonNull(balance);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime timestamp) {
        this.createdAt = requireNonNull(timestamp);
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(final Long account) {
        this.accountId = requireNonNull(account);
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(final TransactionType transactionType) {
        this.transactionType = requireNonNull(transactionType);
    }
}
