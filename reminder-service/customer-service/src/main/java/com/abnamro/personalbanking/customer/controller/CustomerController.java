package com.abnamro.personalbanking.customer.controller;

import com.abnamro.personalbanking.customer.domain.CustomerRequest;
import com.abnamro.personalbanking.customer.domain.Response;
import com.abnamro.personalbanking.customer.service.AccountsService;
import com.abnamro.personalbanking.customer.exception.BusinessException;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/customer-service")
@Validated
public class CustomerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private AccountsService accountsService;

    /**
     * This method is used to create Customer records in DB.
     * @param request of type CustomerRequest
     * @return Response
     * @throws BusinessException
     */
    @RequestMapping(value = "/customer", method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response createCustomer(@RequestBody @Valid CustomerRequest request) throws Exception {
        LOGGER.debug("Request received to create Customers");
           return accountsService.saveCustomer(request);
    }

    /**
     * This method is used to get all Customers details from DB.
     * @return List of type Customers in Response
     */
    @GetMapping("customers/all")
    public ResponseEntity<?> getAllCustomers() {
        LOGGER.debug("Request received to get all Customers");
        LocalDateTime before = LocalDateTime.now();
        List<CustomerRequest> customers = null;
        try {
            customers = accountsService.getAll();
        }catch (Exception exception) {
            LOGGER.error("Unexpected error occurred: ", exception);
        } finally {
            LocalDateTime after = LocalDateTime.now();
            Duration duration = Duration.between(before, after);
            LOGGER.info("Completed Getting All Customers. Time taken to process the request :: "+
                    + duration.getSeconds() + "." + duration.toMillis());
        }
        if (CollectionUtils.isEmpty(customers)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.builder().
                    statusName(HttpStatus.NOT_FOUND.name()).
                    statusCode(HttpStatus.NOT_FOUND.value()).message("No Records Found.").build());

        } else {
            return ResponseEntity.status(HttpStatus.OK).body(customers);
        }
    }

    /**
     * This method is used to get maturity Customers details from DB.
     * @return List of type Customers in Response
     */
    @GetMapping("customers/maturity")
    public Response getMaturityCustomers(@RequestParam(value = "priorMonth", required = false)
                                             Integer priorMonth) {
        LOGGER.debug("Request received to get maturity Customers");
        LocalDateTime before = LocalDateTime.now();
        List<CustomerRequest> customers = null;
        try {
            //Default to current month
            if (priorMonth == null) {
                priorMonth = 0;
            }
            customers = accountsService.getMaturityCustomers(priorMonth);
        }catch (Exception exception) {
            LOGGER.error("Unexpected error occurred: ", exception);
        } finally {
            LocalDateTime after = LocalDateTime.now();
            Duration duration = Duration.between(before, after);
            LOGGER.info("Completed Getting Maturity Customers. Time taken to process the request :: "+
                    + duration.getSeconds() + "." + duration.toMillis());
        }
        if (CollectionUtils.isEmpty(customers)) {
            return Response.builder().
                    statusName(HttpStatus.NOT_FOUND.name()).
                    statusCode(HttpStatus.NOT_FOUND.value()).message("No Records Found.").build();
        } else {
            return Response.builder().
                    statusName(HttpStatus.OK.name()).
                    statusCode(HttpStatus.OK.value()).data(customers).build();
        }
    }

}
