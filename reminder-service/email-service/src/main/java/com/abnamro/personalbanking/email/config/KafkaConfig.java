package com.abnamro.personalbanking.email.config;

import java.util.HashMap;
import java.util.Map;

import com.abnamro.personalbanking.email.exception.KafkaErrorHandler;
import com.abnamro.personalbanking.email.domains.MaturityEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
class KafkaConfig {

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    ConsumerFactory<String, MaturityEvent> consumerFactory(
            @Value("${spring.kafka.consumer.bootstrap-servers}") String bootstrapServers
    ) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(MaturityEvent.class)
        );
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, MaturityEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, MaturityEvent> consumerFactory,
            CommonErrorHandler commonErrorHandler
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, MaturityEvent>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(commonErrorHandler);
        return factory;
    }

    @Bean
    CommonErrorHandler kafkaErrorHandler() {
        return new KafkaErrorHandler();
    }

}