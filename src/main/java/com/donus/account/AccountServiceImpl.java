package com.donus.account;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static java.util.Objects.requireNonNull;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(final AccountRepository accountRepository) {
        this.accountRepository = requireNonNull(accountRepository, "Account repository required");
    }

    @Override
    public Mono<Account> createAccount(final Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Mono<Account> updateAccount(final Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Mono<Account> findAccount(final Long id) {
        return accountRepository.findById(id);
    }

}
