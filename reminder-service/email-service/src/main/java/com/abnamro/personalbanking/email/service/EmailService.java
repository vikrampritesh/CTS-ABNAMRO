package com.abnamro.personalbanking.email.service;

import com.abnamro.personalbanking.email.domains.MaturityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(MaturityEvent maturityEvent) {
        LOGGER.info("Sending email for maturity customer: " + maturityEvent.getCustomerName());
        //TODO - plugin email
        //sendEmail(maturityEvent);
    }

}
