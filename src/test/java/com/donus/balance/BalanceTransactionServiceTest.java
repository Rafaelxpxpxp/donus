package com.donus.balance;

import com.donus.AbstractTest;
import com.donus.account.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class BalanceTransactionServiceTest extends AbstractTest {

    @Autowired
    private BalanceTransactionService balanceTransactionService;

    private Account account;

    @BeforeEach
    public void setUp() {
        account = createAccount("90656280026");
    }

    @AfterEach
    public void clean() {
        cleanDb();
    }

    @Test
    public void shouldDepositWithBonus() {
        //WHEN
        balanceTransactionService.deposit(account.getId(), BigDecimal.ONE).block();

        //THEN
        final BalanceTransaction balanceTransaction = balanceTransactionRepository.findAll().blockFirst();
        assertEquals(account.getId(), balanceTransaction.getAccountId());
        assertEquals(balanceTransaction.getAmount(), BigDecimal.ONE);
        assertEquals(balanceTransaction.getBalance(), BigDecimal.ONE);
        assertEquals(balanceTransaction.getTransactionType(), TransactionType.DEPOSIT);

        final BalanceTransaction bonusTransaction = balanceTransactionRepository.findAll().blockLast();
        assertEquals(account.getId(), bonusTransaction.getAccountId());
        assertEquals(bonusTransaction.getAmount(), new BigDecimal("0.005"));
        assertEquals(bonusTransaction.getBalance(), new BigDecimal("1.005"));
        assertEquals(bonusTransaction.getTransactionType(), TransactionType.DEPOSIT_BONUS);
    }

    @Test
    public void shouldWithdrawWithTax() {
        //GIVEN
        balanceTransactionService.deposit(account.getId(), BigDecimal.TEN).block();

        //WHEN
        balanceTransactionService.withdraw(account.getId(), BigDecimal.valueOf(-1)).block();

        //THEN
        final Iterator<BalanceTransaction> iterator = balanceTransactionRepository.findAll().toIterable().iterator();

        final BalanceTransaction deposit = iterator.next();
        assertEquals(account.getId(), deposit.getAccountId());
        assertEquals(deposit.getAmount(), BigDecimal.TEN);
        assertEquals(deposit.getBalance(), BigDecimal.TEN);
        assertEquals(deposit.getTransactionType(), TransactionType.DEPOSIT);


        final BalanceTransaction depositBonus = iterator.next();
        assertEquals(account.getId(), depositBonus.getAccountId());
        assertEquals(depositBonus.getAmount(), new BigDecimal("0.050"));
        assertEquals(depositBonus.getBalance(), new BigDecimal("10.050"));
        assertEquals(depositBonus.getTransactionType(), TransactionType.DEPOSIT_BONUS);


        final BalanceTransaction withdraw = iterator.next();
        assertEquals(account.getId(), withdraw.getAccountId());
        assertEquals(withdraw.getAmount(), BigDecimal.valueOf(-1));
        assertEquals(withdraw.getBalance(), new BigDecimal("9.050"));
        assertEquals(withdraw.getTransactionType(), TransactionType.WITHDRAW);


        final BalanceTransaction withdrawTax = iterator.next();
        assertEquals(account.getId(), withdrawTax.getAccountId());
        assertEquals(withdrawTax.getAmount(), new BigDecimal("0.01").negate());
        assertEquals(withdrawTax.getBalance(), new BigDecimal("9.040"));
        assertEquals(withdrawTax.getTransactionType(), TransactionType.WITHDRAW_TAX);
    }

    @Test
    public void shouldNotWithdrawWithNoBalance() {
        try {
            balanceTransactionService.withdraw(account.getId(), BigDecimal.valueOf(-1)).block();
        } catch (final IllegalArgumentException e) {
            assertEquals(e.getMessage(), "You don't have enough balance");
            return;
        }
        fail();
    }

    @Test
    public void shouldTransfer() {
        //Given
        balanceTransactionService.deposit(account.getId(), BigDecimal.ONE).block();

        //THEN
        final Account destination = createAccount("90379338076");
        balanceTransactionService.transferBalance(this.account.getId(), destination.getId(), BigDecimal.ONE).block();

        final Iterator<BalanceTransaction> iterator = balanceTransactionRepository.findAll().toIterable().iterator();

        final BalanceTransaction deposit = iterator.next();
        assertEquals(this.account.getId(), deposit.getAccountId());
        assertEquals(deposit.getAmount(), BigDecimal.ONE);
        assertEquals(deposit.getBalance(), BigDecimal.ONE);
        assertEquals(deposit.getTransactionType(), TransactionType.DEPOSIT);

        final BalanceTransaction depositBonus = iterator.next();
        assertEquals(this.account.getId(), depositBonus.getAccountId());
        assertEquals(depositBonus.getAmount(), new BigDecimal("0.005"));
        assertEquals(depositBonus.getBalance(), new BigDecimal("1.005"));
        assertEquals(depositBonus.getTransactionType(), TransactionType.DEPOSIT_BONUS);

        final BalanceTransaction transferOrigin = iterator.next();
        assertEquals(this.account.getId(), transferOrigin.getAccountId());
        assertEquals(transferOrigin.getAmount(), BigDecimal.ONE.negate());
        assertEquals(transferOrigin.getBalance(), new BigDecimal("0.005"));
        assertEquals(transferOrigin.getTransactionType(), TransactionType.TRANSFER);


        final BalanceTransaction transferDestination = iterator.next();
        assertEquals(destination.getId(), transferDestination.getAccountId());
        assertEquals(transferDestination.getAmount(), BigDecimal.ONE);
        assertEquals(transferDestination.getBalance(), BigDecimal.ONE);
        assertEquals(transferDestination.getTransactionType(), TransactionType.TRANSFER);

    }
}