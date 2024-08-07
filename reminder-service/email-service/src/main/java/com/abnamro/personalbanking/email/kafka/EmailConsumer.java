package com.abnamro.personalbanking.email.kafka;

import com.abnamro.personalbanking.email.domains.MaturityEvent;
import com.abnamro.personalbanking.email.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailConsumer.class);

    private final EmailService emailService;

    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    //@KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    @KafkaListener(topics = "${spring.kafka.topic.name}")
    public void consume(MaturityEvent event){
        LOGGER.info(String.format("Email event received => %s", event.toString()));
        emailService.sendEmail(event);
    }
}
