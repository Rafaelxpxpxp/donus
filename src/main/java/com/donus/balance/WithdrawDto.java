package com.donus.balance;

import com.sun.istack.NotNull;

import javax.validation.constraints.Negative;
import java.math.BigDecimal;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WithdrawDto that = (WithdrawDto) o;
        return Objects.equals(accountId, that.accountId) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, value);
    }
}
