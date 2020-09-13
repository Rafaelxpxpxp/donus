package com.donus.account;

import com.donus.kafka.sender.KafkaSender;
import com.donus.kafka.sender.TopicNames;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/account")
public class AccountController {
    private final KafkaSender kafkaSender;

    public AccountController(final KafkaSender kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

    @PostMapping
    public Mono<ResponseEntity<?>> createAccount(@RequestBody @Validated final AccountDto accountDto) {
        return kafkaSender.send(TopicNames.NEW_ACCOUNT_TOPIC, accountDto)
                .map(empty -> ResponseEntity.accepted().build());
    }
}