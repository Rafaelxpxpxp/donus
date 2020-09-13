package com.donus.balance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import org.checkerframework.checker.index.qual.Positive;

import java.math.BigDecimal;

public class TransferDto {
    @NotNull
    private final Long originAccountId;

    @NotNull
    private final Long destinationAccountId;

    @Positive
    @NotNull
    private final BigDecimal value;

    @JsonCreator
    public TransferDto(@JsonProperty("originAccountId") final Long originAccountId,
                       @JsonProperty("destinationAccountId") final Long destinationAccountId,
                       @JsonProperty("value") final BigDecimal value) {
        this.originAccountId = originAccountId;
        this.destinationAccountId = destinationAccountId;
        this.value = value;
    }

    public Long getOriginAccountId() {
        return originAccountId;
    }

    public Long getDestinationAccountId() {
        return destinationAccountId;
    }

    public BigDecimal getValue() {
        return value;
    }
}
