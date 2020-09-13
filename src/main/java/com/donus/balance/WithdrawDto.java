package com.donus.balance;

import com.sun.istack.NotNull;

import javax.validation.constraints.Negative;
import java.math.BigDecimal;

public class WithdrawDto {
    @NotNull
    private final Long accountId;

    @Negative
    @NotNull
    private final BigDecimal value;

    public WithdrawDto(final Long accountId, final BigDecimal value) {
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
