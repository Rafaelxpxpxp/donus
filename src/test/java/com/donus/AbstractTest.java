package com.donus;

import com.donus.account.Account;
import com.donus.account.AccountListener;
import com.donus.account.AccountRepository;
import com.donus.account.AccountService;
import com.donus.balance.BalanceTransactionRepository;
import com.donus.balance.DepositTransactionListener;
import com.donus.balance.TransferTransactionListener;
import com.donus.kafka.sender.KafkaSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.AutoConfigureDataR2dbc;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Random;

@WebFluxTest
@AutoConfigureDataR2dbc
@EnableTransactionManagement
@ActiveProfiles("test")
@ComponentScan(basePackages = "com.donus")
public class AbstractTest {

    @MockBean
    protected KafkaSender kafkaSender;

    @Autowired
    protected WebTestClient webClient;

    @MockBean
    protected TransferTransactionListener transferTransactionListener;

    @MockBean
    protected DepositTransactionListener depositTransactionListener;

    @MockBean
    protected AccountListener accountListener;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    protected BalanceTransactionRepository balanceTransactionRepository;



    public Account createAccount(final String cpf) {
        return accountService.createAccount(new Account(createRandomName(), cpf))
                .block();
    }

    protected void cleanDb() {
        balanceTransactionRepository.deleteAll().block();
        accountRepository.deleteAll().block();
    }

    private static String createRandomName() {
        final char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        final StringBuilder sb = new StringBuilder(20);
        final Random random = new Random();
        for (int i = 0; i < 20; i++) {
            final char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
}
