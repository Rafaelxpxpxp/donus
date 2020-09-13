package com.donus.kafka.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

@Component
public class KafkaSender {
    private final ReactiveKafkaProducerTemplate<String, String> template;
    private final ObjectMapper objectMapper;

    public KafkaSender(final ReactiveKafkaProducerTemplate<String, String> template,
                       final ObjectMapper objectMapper) {
        this.template = template;
        this.objectMapper = objectMapper;
    }

    public Mono<SenderResult<Void>> send(final String topic, final Object message)  {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(message))
                .flatMap(msg -> template.send(topic, msg));
    }
}
