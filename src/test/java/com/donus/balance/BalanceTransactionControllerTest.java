package com.donus.balance;

import com.donus.AbstractTest;
import com.donus.kafka.sender.TopicNames;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BalanceTransactionControllerTest extends AbstractTest {

    @MockBean
    private BalanceTransactionService balanceTransactionService;

    @Test
    public void shouldNotAcceptNegativeValuesForDeposit() {
        //Given
        final DepositDto depositDto = new DepositDto(1L, BigDecimal.valueOf(-1));

        //When
        webClient.post()
                .uri("/balance/transaction/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(depositDto))
                .exchange()
                //THEN
                .expectStatus().isBadRequest();

    }

    @Test
    public void shouldNotAcceptPositiveValuesForWithdraw() {
        //Given
        final DepositDto withdrawDto = new DepositDto(1L, BigDecimal.ONE);

        //When
        webClient.post()
                .uri("/balance/transaction/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(withdrawDto))
                .exchange()
                //THEN
                .expectStatus().isBadRequest();

    }

    @Test
    public void shouldSendToKafkaWhenValueIsCorrectForDeposit(){
        //Given
        final DepositDto depositDto = new DepositDto(1L, BigDecimal.ONE);

        when(kafkaSender.send(eq(TopicNames.DEPOSIT_TOPIC), eq(depositDto)))
                .thenReturn(Mono.just(mock(SenderResult.class)));

        //When
        webClient.post()
                .uri("/balance/transaction/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(depositDto))
                .exchange()
                //THEN
                .expectStatus().isAccepted();

        verify(kafkaSender).send(eq(TopicNames.DEPOSIT_TOPIC), eq(depositDto));

    }

    @Test
    public void shouldSendToKafkaWhenValueIsCorrectForWithdraw(){
        //Given
        final WithdrawDto withdrawDto = new WithdrawDto(1L, BigDecimal.valueOf(-1));

        when(balanceTransactionService.withdraw(eq(withdrawDto.getAccountId()), eq(withdrawDto.getValue()))).thenReturn(Mono.empty());

        //When
        webClient.post()
                .uri("/balance/transaction/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(withdrawDto))
                .exchange()
                //THEN
                .expectStatus().isOk();

        verify(balanceTransactionService).withdraw(eq(withdrawDto.getAccountId()), eq(withdrawDto.getValue()));

    }
}