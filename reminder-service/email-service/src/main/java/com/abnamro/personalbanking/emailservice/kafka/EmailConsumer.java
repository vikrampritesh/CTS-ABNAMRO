package com.abnamro.personalbanking.emailservice.kafka;

import com.abnamro.personalbanking.emailservice.domains.MaturityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailConsumer.class);

    @KafkaListener(
            topics = "${spring.kafka.topic.name}"
            ,groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(MaturityEvent event){
        LOGGER.info(String.format("Email event received => %s", event.toString()));
        //TODO - Send Maturity event to the EmailService
    }
}
