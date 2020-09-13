package com.donus.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class AccountDto {

    @NotBlank
    private final String name;

    @CPF
    @NotNull
    private final String documentId;

    @NotNull
    @PositiveOrZero
    private final BigDecimal balance;

    @JsonCreator
    public AccountDto(@JsonProperty("name") final String name,
                      @JsonProperty("documentId") final String documentId,
                      @JsonProperty("balance") final BigDecimal balance) {
        this.name = name;
        this.documentId = documentId;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public String getDocumentId() {
        return documentId;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
