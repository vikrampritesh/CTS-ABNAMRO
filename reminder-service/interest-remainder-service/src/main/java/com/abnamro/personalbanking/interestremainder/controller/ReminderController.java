package com.abnamro.personalbanking.interestremainder.controller;

import com.abnamro.personalbanking.interestremainder.domains.*;
import com.abnamro.personalbanking.interestremainder.enums.AccountType;
import com.abnamro.personalbanking.interestremainder.enums.ReminderStatus;
import com.abnamro.personalbanking.interestremainder.exception.BusinessException;
import com.abnamro.personalbanking.interestremainder.kafka.MaturityProducer;
import com.abnamro.personalbanking.interestremainder.utils.CommonConstants;
import com.abnamro.personalbanking.interestremainder.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/interest-remainder-service")
@Validated
public class ReminderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReminderController.class);

    private MaturityProducer maturityProducer;

    ReminderController(MaturityProducer maturityProducer) {
        this.maturityProducer = maturityProducer;
    }

    @Value("${customer-service-url}")
    private String customerServiceUrl;

    /**
     * This method is used to remind the maturity bank deposit customers of
     * the next month by calling Customer Service.
     * @param remainder of type Remainder
     * @return response of type Response
     * @throws BusinessException
     */
    @PostMapping("/remind")
    public Response placeReminder(@RequestBody @Valid Remainder remainder) throws Exception {
        LOGGER.debug("Started Placing Fixed Deposit Reminder.");
        processReminder(remainder);
        LOGGER.info(CommonConstants.MATURITY_EVENT_SUCCESS_MSG);
        return Response.builder().statusName(HttpStatus.OK.name()).
                statusCode(HttpStatus.OK.value()).
                message(CommonConstants.MATURITY_EVENT_SUCCESS_MSG).build();
    }

    /**
     * This method is used to remind the maturity bank deposit customers of
     * the next month by calling Customer Service.
     * @param remainder of type Remainder
     * @return response of type Response
     */
    private void processReminder(Remainder remainder) throws Exception {
        if (remainder == null) {
            throw BusinessException.builder().
                    message(CommonConstants.MISSING_REMINDER_MSG).
                    status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if (AccountType.FIXED.name().equalsIgnoreCase(remainder.getAccountType())) {
                remainder.setStatus(ReminderStatus.STARTED.name());
                LOGGER.debug("Calling Customer Service.");
                RestClient restClient = RestClient.create();
                Response response = restClient.get()
                        .uri(customerServiceUrl)
                        .retrieve()
                        .body(Response.class);
                if (response != null) {
                    if (HttpStatus.OK.value() != response.getStatusCode()) {
                        throw BusinessException.builder().
                                message(CommonConstants.NO_MATURITY_MSG).
                                status(HttpStatus.NOT_FOUND).build();
                    }
                    for (CustomerRequest customer : CommonUtils.convertResponse(response.getData())) {
                        for (AccountDto accountDto : customer.getAccounts()) {
                            if (accountDto instanceof FixedDepositAccountDto) {
                                MaturityEvent event = MaturityEvent.builder().
                                        eventId(UUID.randomUUID().toString()).
                                        correlationId(UUID.randomUUID().toString()).
                                        maturityAmount(((FixedDepositAccountDto) accountDto).getMaturityAmount()).
                                        dateOfMaturity(((FixedDepositAccountDto) accountDto).getMaturityDate()).
                                        customerName(customer.getFirstName() + " " + customer.getLastName()).
                                        remainder(remainder).
                                        build();
                                remainder.setId(UUID.randomUUID().toString());
                                remainder.setStatus(ReminderStatus.PROGRESS.name());
                                remainder.setRemainderText(CommonConstants.REMINDER_MSG);
                                maturityProducer.sendMessage(event);
                                remainder.setStatus(ReminderStatus.SENT.name());
                            }
                        }
                    }
                }
            } else {
                throw BusinessException.builder().
                        message(CommonConstants.NO_RECORDS_FOND_MSG).
                        status(HttpStatus.NOT_FOUND).build();
            }
        }
        catch (Exception ex) {
            LOGGER.error("Unexpected error occurred: ", ex);
            throw ex;
        }
    }

}
