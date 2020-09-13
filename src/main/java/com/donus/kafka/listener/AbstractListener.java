package com.donus.kafka.listener;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractListener {

    //Hold reference to avoid garbage colector
    private final KafkaReceiver<String, String> kafkaReceiver;

    protected AbstractListener(final Map<String, Map<String, Object>> kafkaConsumerProps) {
        kafkaReceiver = createListener(kafkaConsumerProps);
    }


    private KafkaReceiver<String, String> createListener(final Map<String, Map<String, Object>> kafkaConsumerProps) {
        final Map<String, Object> copyKafkaConsumerProps = new HashMap<>(kafkaConsumerProps.get("kafkaConsumerProps"));

        copyKafkaConsumerProps.put(ConsumerConfig.CLIENT_ID_CONFIG, "DONUS_SERVICE-" + UUID.randomUUID().toString());

        final ReceiverOptions<String, String> receiverOptions =
                ReceiverOptions.<String, String>create(copyKafkaConsumerProps)
                        .subscription(Collections.singleton(getTopicName()));
        final KafkaReceiver<String, String> kafkaReceiver = KafkaReceiver.create(receiverOptions);

        kafkaReceiver.receive()
                .flatMap(this::handle)
                .subscribeOn(Schedulers.elastic())
                .subscribe();

        return kafkaReceiver;
    }

    protected abstract String getTopicName();

    protected abstract Mono<Void> handle(final ReceiverRecord<String, String> depositDtoRecord);

}
