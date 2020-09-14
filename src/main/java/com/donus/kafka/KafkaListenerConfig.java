package com.donus.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

@Profile("!test")
@Configuration
public class KafkaListenerConfig {

    @Bean("kafkaConsumerProps")
    public Map<String, Object> createConsumerProps(
            @Value("${spring.kafka.bootstrap-servers}") final String bootstrapServers){
        final Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "DONUS_SERVICE");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return consumerProps;
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, String> reactiveKafkaProducerTemplate(
            final KafkaProperties properties) {
        final Map<String, Object> props = properties.buildProducerProperties();
        props.put(
                ProducerConfig.TRANSACTIONAL_ID_CONFIG, properties.getProducer().getTransactionIdPrefix());
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
    }
}
