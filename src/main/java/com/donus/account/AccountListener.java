package com.donus.account;

import com.donus.kafka.listener.AbstractListener;
import com.donus.kafka.sender.TopicNames;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.Map;

@Component
public class AccountListener extends AbstractListener {

    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final AccountService accountService;

    public AccountListener(final ModelMapper modelMapper,
                           final Map<String, Map<String, Object>> kafkaConsumerProps,
                           final ObjectMapper objectMapper,
                           final AccountService accountService) {
        super(kafkaConsumerProps);
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        this.accountService = accountService;
    }

    @Override
    protected String getTopicName() {
        return TopicNames.NEW_ACCOUNT_TOPIC;
    }

    @Override
    protected Mono<Void> handle(final ReceiverRecord<String, String> depositDtoRecord) {
        return Mono.fromCallable(() -> objectMapper.readValue(depositDtoRecord.value(), AccountDto.class))
                .map(accountDto -> modelMapper.map(accountDto, Account.class))
                .flatMap(accountService::createAccount)
                .then();
    }
}
