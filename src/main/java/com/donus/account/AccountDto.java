package com.donus.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Objects;

public class AccountDto {

    @NotBlank
    @Pattern(regexp = "[a-zA-Z\\u00C0-\\u017F\\s]+")
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AccountDto that = (AccountDto) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(documentId, that.documentId) &&
                Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, documentId, balance);
    }
}
