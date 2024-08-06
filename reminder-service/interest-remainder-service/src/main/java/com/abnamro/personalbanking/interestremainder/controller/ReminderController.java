package com.abnamro.personalbanking.interestremainder.controller;

import com.abnamro.personalbanking.basedomains.domain.*;
import com.abnamro.personalbanking.basedomains.enums.AccountType;
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
import java.util.Date;
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

    @Value("${base-domains.customer-service-url}")
    private String customerServiceUrl;

    /**
     * This method is used to remind the maturity bank deposit customers of
     * the next month by calling Customer Service.
     * @param remainder of type Remainder
     * @return response of type Response
     * @throws BusinessException
     */
    @PostMapping("/remind")
    public Response placeReminder(@RequestBody @Valid Remainder remainder) throws BusinessException {
        LOGGER.debug("Started Placing Fixed Deposit Reminder.");
        try {
            processReminder(remainder);
        }
        catch (BusinessException bixEx) {
            LOGGER.error("BusinessException Occurred: "+bixEx.getMessage());
            return Response.builder().statusName(bixEx.getStatus().name()).
                    statusCode(bixEx.getStatus().value()).
                    message(bixEx.getMessage()).build();
        }
        catch (Exception ex) {
                LOGGER.error("Unexpected error occurred: ",ex);
                return Response.builder().statusName(HttpStatus.INTERNAL_SERVER_ERROR.name()).
                        statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).
                        message(CommonConstants.MATURITY_EVENT_FAIL_MSG+ex.getMessage()).build();
        }
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
            if (AccountType.FIXED.name().equalsIgnoreCase(remainder.getTypeOfAccount())) {
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
