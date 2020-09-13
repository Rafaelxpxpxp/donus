package com.donus.balance;

import com.donus.kafka.listener.AbstractListener;
import com.donus.kafka.sender.TopicNames;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.retry.Retry;

import javax.persistence.OptimisticLockException;
import java.time.Duration;
import java.util.Map;

@Component
public class TransferTransactionListener extends AbstractListener {

    private final BalanceTransactionService balanceTransactionService;

    private final ObjectMapper objectMapper;

    private final RedisTemplate<String, String> redisTemplate;

    private static final int MAX_ATTEMPTS = 100;
    private static final Duration RETRY_BACKOFF = Duration.ofSeconds(1);
    private static final String ACCOUNT_LOCK_KEY = "ACCOUNT_LOCK_KEY:%s";
    private static final Logger LOGGER = LoggerFactory.getLogger(TransferTransactionListener.class);

    public TransferTransactionListener(final BalanceTransactionService balanceTransactionService,
                                       final ObjectMapper objectMapper,
                                       final Map<String, Map<String, Object>> kafkaConsumerProps,
                                       final RedisTemplate<String, String> redisTemplate) {
        super(kafkaConsumerProps);
        this.balanceTransactionService = balanceTransactionService;
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected String getTopicName() {
        return TopicNames.TRANSFER_TOPIC;
    }

    @Override
    protected Mono<Void> handle(final ReceiverRecord<String, String> record) {
        return Mono.fromCallable(() -> objectMapper.readValue(record.value(), TransferDto.class))
                .doOnNext(transferDto -> {
                    lockAccount(transferDto.getOriginAccountId());
                    lockAccount(transferDto.getDestinationAccountId());
                })
                .doOnError(throwable -> LOGGER.error(throwable.getMessage()))
                .retryWhen(Retry.backoff(MAX_ATTEMPTS, RETRY_BACKOFF))
                .flatMap(transferDto -> balanceTransactionService.transferBalance(transferDto.getOriginAccountId(),
                        transferDto.getDestinationAccountId(),
                        transferDto.getValue()))
                .doOnSuccess(transfers -> {
                    final Long originAccountId = transfers.getT1().getAccountId();
                    unlockAccount(originAccountId);
                    final Long destinationAccountId = transfers.getT2().getAccountId();
                    unlockAccount(destinationAccountId);
                    record.receiverOffset().acknowledge();
                })
                .then();
    }

    private void lockAccount(final Long accountId) {
        final String lockOriginKey = String.format(ACCOUNT_LOCK_KEY, accountId);
        if (redisTemplate.hasKey(lockOriginKey)) {
            throw new OptimisticLockException("Account already doing operation");
        }
        redisTemplate.opsForValue()
                .set(lockOriginKey, TopicNames.TRANSFER_TOPIC);
    }

    private void unlockAccount(final Long accountId){
        final String lockOriginKey = String.format(ACCOUNT_LOCK_KEY, accountId);
        redisTemplate.delete(lockOriginKey);
    }
}
