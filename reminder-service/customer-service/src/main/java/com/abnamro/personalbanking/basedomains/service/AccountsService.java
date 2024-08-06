package com.abnamro.personalbanking.basedomains.service;

import com.abnamro.personalbanking.basedomains.domain.CustomerRequest;
import com.abnamro.personalbanking.basedomains.domain.Response;
import com.abnamro.personalbanking.basedomains.exception.BusinessException;

import java.time.LocalDate;
import java.util.List;

public interface AccountsService {

    public Response saveCustomer(CustomerRequest request) throws Exception;
    public List<CustomerRequest> getMaturityCustomers(Integer priorMonth);

    /**
     * This method is used to get all Customers details from DB.
     * @return List of type CustomerRequest
     */
    public List<CustomerRequest> findAll();

}
