package com.abnamro.personalbanking.interestremainder.kafka;

import com.abnamro.personalbanking.basedomains.domain.MaturityEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class MaturityProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaturityProducer.class);

    private NewTopic topic;

    private KafkaTemplate<String, MaturityEvent> kafkaTemplate;

    public MaturityProducer(NewTopic topic, KafkaTemplate<String, MaturityEvent> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(MaturityEvent event){
        LOGGER.info(String.format("Maturity event => %s", event.toString()));
        // create Message
        Message<MaturityEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();
        kafkaTemplate.send(message);
    }
}
