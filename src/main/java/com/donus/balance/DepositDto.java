package com.donus.balance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import org.checkerframework.checker.index.qual.Positive;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

public class DepositDto {
    @NotNull
    private final Long accountId;

    @Positive
    @NotNull
    private final BigDecimal value;

    @JsonCreator
    public DepositDto(@JsonProperty("accountId") final Long accountId,
                      @JsonProperty("value") final BigDecimal value) {
        this.accountId = accountId;
        this.value = value;
    }

    public Long getAccountId() {
        return accountId;
    }

    public BigDecimal getValue() {
        return value;
    }
}
