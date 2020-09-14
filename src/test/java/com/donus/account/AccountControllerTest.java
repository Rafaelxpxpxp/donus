package com.donus.account;

import com.donus.AbstractTest;
import com.donus.kafka.sender.TopicNames;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AccountControllerTest extends AbstractTest {

    @ParameterizedTest
    @DisplayName("Should not accept invalid CPFs")
    @ValueSource(strings = {"abcdef", "00000000000", "12345678910", "", "9106070019"})
    public void shouldNotAcceptInvalidCPFs(final String invalidCpf) {
        //Given
        final AccountDto accountDto = new AccountDto("Rafael", invalidCpf, BigDecimal.ZERO);

        //When
        webClient.post()
                .uri("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(accountDto))
                .exchange()
                //THEN
                .expectStatus().isBadRequest();

    }

    @ParameterizedTest
    @DisplayName("Should accept valid CPFs")
    @ValueSource(strings = {"09106070019", "92005770086"})
    public void shouldAcceptValidCPFs(final String validCpf) {
        //Given
        final AccountDto accountDto = new AccountDto("Rafael Ferreira", validCpf, BigDecimal.ZERO);

        when(kafkaSender.send(eq(TopicNames.NEW_ACCOUNT_TOPIC), eq(accountDto)))
                .thenReturn(Mono.just(mock(SenderResult.class)));

        //When
        webClient.post()
                .uri("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(accountDto))
                .exchange()
                //THEN
                .expectStatus().isAccepted();

        verify(kafkaSender).send(eq(TopicNames.NEW_ACCOUNT_TOPIC), eq(accountDto));

    }



    @ParameterizedTest
    @DisplayName("Should not accept invalid names")
    @ValueSource(strings = {"", "123Rafael", "陳大文"})
    public void shouldNotAcceptInvalidName(final String invalidName) {
        //Given
        final AccountDto accountDto = new AccountDto(invalidName, "92005770086", BigDecimal.ZERO);

        //When
        webClient.post()
                .uri("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(accountDto))
                .exchange()
                //THEN
                .expectStatus().isBadRequest();

    }

    @Test
    @DisplayName("Should not accept negative balance values")
    public void shouldNotAcceptNegativeValues() {
        //Given
        final AccountDto accountDto = new AccountDto("Rafael", "92005770086", BigDecimal.valueOf(-1));

        //When
        webClient.post()
                .uri("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(accountDto))
                .exchange()
                //THEN
                .expectStatus().isBadRequest();

    }
}