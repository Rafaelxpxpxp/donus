package com.donus.balance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Objects;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DepositDto that = (DepositDto) o;
        return Objects.equals(accountId, that.accountId) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, value);
    }
}
