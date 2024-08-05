package com.abnamro.personalbanking.interestremainder.controller;

import com.abnamro.personalbanking.basedomains.domain.*;
import com.abnamro.personalbanking.basedomains.enums.AccountType;
import com.abnamro.personalbanking.basedomains.service.AccountsService;
import com.abnamro.personalbanking.interestremainder.exception.BusinessException;
import com.abnamro.personalbanking.interestremainder.kafka.MaturityProducer;
import com.abnamro.personalbanking.interestremainder.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/interest-remainder-service")
@Validated
public class ReminderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReminderController.class);

    private MaturityProducer maturityProducer;

    @Autowired //(required=true)
    private RestClient restClient;

    /*public ReminderController(MaturityProducer maturityProducer, RestClient restClient) {
        this.maturityProducer = maturityProducer;
        this.restClient = restClient;
    }*/

    @GetMapping("/status")
    public String getStatus() {
        return "Up :: "+new Date();
    }

    @PostMapping("/remind")
    public Response placeReminder(@RequestBody @Valid Remainder remainder) throws BusinessException {
        if (remainder == null) {
            throw BusinessException.builder().
                    message("Missing Remainder Details.").
                    status(HttpStatus.BAD_REQUEST).build();
        }
        if (AccountType.FIXED.name().equalsIgnoreCase(remainder.getTypeOfAccount())) {
            remainder.setStatus("STARTED");
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            headers.add(HttpHeaders.COOKIE, "false");
            String path = "http://localhost:8080/customer-service/customers/maturity?priorMonth=0";

            List<CustomerRequest> customers = CommonUtils.invokeRestApi(null, path, restClient,
                    headers, HttpMethod.GET, false,
                    new ParameterizedTypeReference<List<CustomerRequest>>() {
                    });
            for (CustomerRequest customer : customers) {
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
                        remainder.setStatus("PROGRESS");
                        maturityProducer.sendMessage(event);
                        remainder.setStatus("SENT");
                    }
                }
            }
        }
        return Response.builder().statusName(HttpStatus.OK.name()).
                statusCode(HttpStatus.OK.value()).
                message("MaturityEvent placed successfully ...").build();
    }



}
