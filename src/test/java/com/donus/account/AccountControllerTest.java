package com.donus.account;

import com.donus.kafka.sender.KafkaSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.math.BigDecimal;

@SpringBootTest
@ActiveProfiles("test")
@ComponentScan(basePackages = "com.donus")
class AccountControllerTest {

    @MockBean
    private KafkaSender kafkaSender;

    @Autowired
    private WebTestClient webClient;

    @Test
    @DisplayName("Should not accept invalid CPF")
    @ValueSource(strings = {"abcdef", "00000000000", "12345678910"}) // six numbers
    public void shouldNotAcceptInvalidCPF(final String invalidCpf) {
        //Given
        AccountDto accountDto = new AccountDto("Rafael", invalidCpf, BigDecimal.ZERO);

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