package com.donus.balance;

import com.donus.kafka.listener.AbstractListener;
import com.donus.kafka.sender.TopicNames;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.Map;

@Component
public class DepositTransactionListener extends AbstractListener {

    private final BalanceTransactionService balanceTransactionService;
    private final ObjectMapper objectMapper;

    public DepositTransactionListener(final BalanceTransactionService balanceTransactionService,
                                      final ObjectMapper objectMapper,
                                      final Map<String, Map<String, Object>> kafkaConsumerProps) {
        super(kafkaConsumerProps);
        this.balanceTransactionService = balanceTransactionService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected String getTopicName() {
        return TopicNames.DEPOSIT_TOPIC;
    }

    protected Mono<Void> handle(final ReceiverRecord<String, String> depositDtoRecord) {
        return Mono.fromCallable(() -> objectMapper.readValue(depositDtoRecord.value(), DepositDto.class))
                .flatMap(depositDto -> balanceTransactionService.deposit(depositDto.getAccountId(), depositDto.getValue()))
                .doOnSuccess(unused -> depositDtoRecord.receiverOffset().acknowledge());
    }
}
