package com.abnamro.personalbanking.customer.service;

import com.abnamro.personalbanking.customer.domain.CustomerRequest;
import com.abnamro.personalbanking.customer.domain.Response;

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
