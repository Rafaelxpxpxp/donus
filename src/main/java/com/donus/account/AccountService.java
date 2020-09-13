package com.donus.account;

import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<Account> createAccount(Account account);

    Mono<Account> updateAccount(Account account);

    Mono<Account> findAccount(Long id);
}
