package com.donus.balance;

import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;

public interface BalanceTransactionService {
    Mono<Tuple2<BalanceTransaction, BalanceTransaction>> transferBalance(Long originAccountId,
                                                                         Long destinationAccountId,
                                                                         BigDecimal value);
    Mono<Void> withdraw(Long accountId, BigDecimal value);
    Mono<Void> deposit(Long accountId, BigDecimal value);
}
