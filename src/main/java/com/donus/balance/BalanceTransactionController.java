package com.donus.balance;

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
@RequestMapping("/balance/transaction")
public class BalanceTransactionController {

    private final KafkaSender kafkaSender;
    private final BalanceTransactionService balanceTransactionService;

    public BalanceTransactionController(final KafkaSender kafkaSender,
                                        final BalanceTransactionService balanceTransactionService) {
        this.kafkaSender = kafkaSender;
        this.balanceTransactionService = balanceTransactionService;
    }

    @PostMapping("/withdraw")
    public Mono<ResponseEntity<?>> withdraw(@RequestBody @Validated final WithdrawDto withdraw) {
        return balanceTransactionService.withdraw(withdraw.getAccountId(), withdraw.getValue())
                .map(unused -> ResponseEntity
                        .accepted()
                        .build());
    }

    @PostMapping("/transfer")
    public Mono<ResponseEntity<?>> transfer(@RequestBody @Validated final TransferDto transferDto) {
        return sendToTopic(transferDto, TopicNames.TRANSFER_TOPIC);
    }

    @PostMapping("/deposit")
    public Mono<ResponseEntity<?>> deposit(@RequestBody @Validated final DepositDto depositDto) {
        return sendToTopic(depositDto, TopicNames.DEPOSIT_TOPIC);
    }

    private Mono<ResponseEntity<?>> sendToTopic(final Object dto, final String topicName) {
        return kafkaSender.send(topicName, dto)
                .map(kafkaResult -> ResponseEntity
                        .accepted()
                        .build());
    }

}
