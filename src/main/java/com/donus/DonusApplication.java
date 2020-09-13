package com.donus;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import reactor.kafka.sender.SenderOptions;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Map;


@SpringBootApplication
@EnableR2dbcRepositories
@EnableTransactionManagement
public class DonusApplication {

	public static void main(final String[] args) {
		SpringApplication.run(DonusApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		final ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper;
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build();
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
