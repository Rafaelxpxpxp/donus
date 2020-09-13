package com.donus.balance;

import com.donus.account.AccountService;
import com.donus.kafka.sender.KafkaSender;
import com.donus.kafka.sender.TopicNames;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

@Service
public class BalanceTransactionServiceImpl implements BalanceTransactionService {

    private final BalanceTransactionRepository balanceTransactionRepository;
    private final AccountService accountService;
    private final KafkaSender kafkaSender;

    public BalanceTransactionServiceImpl(final BalanceTransactionRepository balanceTransactionRepository,
                                         final AccountService accountService,
                                         final KafkaSender kafkaSender) {
        this.balanceTransactionRepository = balanceTransactionRepository;
        this.accountService = accountService;
        this.kafkaSender = kafkaSender;
    }

    @Override
    @Transactional
    public Mono<Tuple2<BalanceTransaction, BalanceTransaction>> transferBalance(final Long originAccountId,
                                                                                final Long destinationAccountId,
                                                                                final BigDecimal value) {
        return accountService.findAccount(originAccountId)
                .flatMap(account -> saveTransaction(value.negate(), account.getBalance(), account.getId(), TransactionType.TRANSFER))
                .zipWith(accountService.findAccount(destinationAccountId)
                        .flatMap(account -> saveTransaction(value, account.getBalance(), account.getId(), TransactionType.TRANSFER)));
    }

    @Override
    @Transactional
    public Mono<Void> withdraw(final Long accountId,
                               final BigDecimal value) {
        return accountService.findAccount(accountId)
                .flatMap(account -> saveTransaction(value, account.getBalance(), account.getId(), TransactionType.WITHDRAW))
                .flatMap(balanceTransaction -> {
                    final BigDecimal taxValue = value.multiply(new BigDecimal("0.01")).negate();
                    return saveTransaction(taxValue, balanceTransaction.getBalance(), balanceTransaction.getAccountId(), TransactionType.WITHDRAW_TAX);
                })
                .doOnSuccess(balanceTransaction -> kafkaSender.send(TopicNames.WITHDRAW_TOPIC, new WithdrawDto(accountId, value)))
                .then();
    }

    private Mono<BalanceTransaction> saveTransaction(final BigDecimal value,
                                                     final BigDecimal balance,
                                                     final Long accountId,
                                                     final TransactionType transactionType) {
        if (!hasBalance(value, balance)) {
            throw new IllegalArgumentException("You don't have enough balance");
        }
        return balanceTransactionRepository.save(new BalanceTransaction(accountId,
                value,
                balance.add(value), transactionType))
                .doOnSuccess(balanceTransaction -> accountService.findAccount(accountId)
                        .flatMap(account -> {
                            account.setBalance(balanceTransaction.getBalance());
                            return accountService.updateAccount(account);
                        }).subscribe()
                );
    }


    private static boolean hasBalance(final BigDecimal value, final BigDecimal balance) {
        return balance.add(value).compareTo(ZERO) >= 0;
    }

    @Override
    @Transactional
    public Mono<Void> deposit(final Long accountId,
                              final BigDecimal value) {
        return accountService.findAccount(accountId)
                .flatMap(account -> saveTransaction(value, account.getBalance(), account.getId(), TransactionType.DEPOSIT))
                .flatMap(balanceTransaction -> {
                    final BigDecimal taxValue = value.multiply(new BigDecimal("0.005"));
                    return saveTransaction(taxValue, balanceTransaction.getBalance(), balanceTransaction.getAccountId(), TransactionType.DEPOSIT_BONUS);
                })
                .then();
    }
}
