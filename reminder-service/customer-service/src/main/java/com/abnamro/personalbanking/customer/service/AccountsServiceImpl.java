package com.abnamro.personalbanking.customer.service;

import com.abnamro.personalbanking.customer.domain.*;
import com.abnamro.personalbanking.customer.enums.AccountStatus;
import com.abnamro.personalbanking.customer.enums.AccountType;
import com.abnamro.personalbanking.customer.exception.BusinessException;
import com.abnamro.personalbanking.customer.model.Account;
import com.abnamro.personalbanking.customer.model.Address;
import com.abnamro.personalbanking.customer.model.Customer;
import com.abnamro.personalbanking.customer.model.FixedDepositAccount;
import com.abnamro.personalbanking.customer.repo.CustomerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AccountsServiceImpl implements AccountsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountsServiceImpl.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public Response saveCustomer(CustomerRequest request) throws Exception {
        Customer model = new Customer();
        validate(request);
        buildtoModel(model, request);
        Customer customer = customerRepository.save(model);
        return Response.builder().statusName(HttpStatus.CREATED.name()).
                statusCode(HttpStatus.CREATED.value()).
                message("Created Customer Successfully").build();
    }

    public List<CustomerRequest> getMaturityCustomers(Integer priorMonth) {
        List<CustomerRequest> customers = this.getAll();
        List<CustomerRequest> maturityCustomers = new ArrayList<>();
        if (customers != null && !CollectionUtils.isEmpty(customers)) {
            for (CustomerRequest customer : customers) {
                if (customer != null && !CollectionUtils.isEmpty(customer.getAccounts())) {
                    for (AccountDto accountDto : customer.getAccounts()) {
                        if (accountDto instanceof FixedDepositAccountDto) {
                            if (((FixedDepositAccountDto) accountDto).isMaturingDepositWithInMonth(priorMonth)) {
                                maturityCustomers.add(customer);
                            }
                        }
                    }
                }
            }
        }
        return maturityCustomers;
    }


    /**
     * This method is used to get all Customers details from DB.
     * @return List of type CustomerRequest
     */
    public List<CustomerRequest> getAll() {
        LOGGER.debug("Request received in service to get all Customers");
        List<CustomerRequest> customers = new ArrayList<>();
        List<Customer> models = customerRepository.findAll();
        if (!CollectionUtils.isEmpty(models)) {
            //models.parallelStream().forEach(model -> customers.add(this.buildtoDto(model)));
            models.stream().forEach(model -> customers.add(this.buildtoDto(model)));
            LOGGER.info("Successfully got all Customers of size ::" + customers.size());
        }
        return customers;
    }

    private void validate(CustomerRequest request) throws BusinessException {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            throw BusinessException.builder().
                    message("Missing Customer Request.").
                    status(HttpStatus.BAD_REQUEST).build();
        }
        if (CollectionUtils.isEmpty(request.getAccounts())) {
            errors.add("Missing accounts");
        }
        else {
            for (AccountDto account : request.getAccounts()) {
                DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate today = LocalDate.parse(LocalDate.now().toString(), parser);
                if (account.getDateOfOpening() == null) {
                    errors.add("Missing dateOfOpening");
                } else {
                    LocalDate dateOfOpening = LocalDate.parse(account.getDateOfOpening(), parser);
                    if (dateOfOpening.isBefore(today)) {
                        //** For Testing to enter junk data, commenting this ***//
                        //errors.add("dateOfOpening shouldn't be previous date");
                    }
                }
                if (account instanceof FixedDepositAccountDto) {
                    if (((FixedDepositAccountDto) account).getMaturityDate() == null) {
                        errors.add("Missing maturityDate");
                    } else {
                        LocalDate maturityDate = LocalDate.parse(((FixedDepositAccountDto) account).getMaturityDate(), parser);
                        //** For Testing to enter junk data, commenting this ***//
                        /*if (maturityDate.isBefore(today)) {
                            errors.add("maturityDate shouldn't be previous date");
                        }*/
                        if (account.getDateOfOpening() != null) {
                            LocalDate dateOfOpening = LocalDate.parse(account.getDateOfOpening(), parser);
                            if (maturityDate.isBefore(dateOfOpening)) {
                                errors.add("maturityDate shouldn't be previous to dateOfOpening");
                            }
                        }
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(errors)) {
            throw BusinessException.builder().
                    message(errors.toString()).
                    status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private void buildtoModel(Customer model, CustomerRequest request){
        BeanUtils.copyProperties(request, model);
        if (!CollectionUtils.isEmpty(request.getAccounts())) {
            List<Account> accountModels = new ArrayList<>();
            for (AccountDto accountDto : request.getAccounts()) {
                if (AccountType.FIXED.name().equalsIgnoreCase(accountDto.getAccountType())) {
                    Account accountModel = new FixedDepositAccount();
                    BeanUtils.copyProperties(accountDto, accountModel);
                    accountModel.setDateCreated(OffsetDateTime.now());
                    accountModel.setStatus(AccountStatus.ACTIVE.name());
                    accountModel.setAccountNumber(UUID.randomUUID().toString());
                    accountModels.add(accountModel);
                }
            }
            model.setAccounts(accountModels);
        }
        if (!CollectionUtils.isEmpty(request.getAddresses())) {
            List<com.abnamro.personalbanking.customer.model.Address> addressModels = new ArrayList<>();
            for (AddressDto addressDto : request.getAddresses()) {
                Address addressModel = new Address();
                BeanUtils.copyProperties(addressDto, addressModel);
                addressModel.setDateCreated(OffsetDateTime.now());
                addressModels.add(addressModel);
            }
            model.setDateCreated(OffsetDateTime.now());
            model.setAddresses(addressModels);
        }
    }

    private CustomerRequest buildtoDto(Customer model) {
        CustomerRequest request = null;
        if (model != null) {
            request = new CustomerRequest();
            BeanUtils.copyProperties(model, request);
            if (!CollectionUtils.isEmpty(model.getAccounts())) {
                List<AccountDto> accountDtos = new ArrayList<>();
                for (Account account : model.getAccounts()) {
                    if ("FIXED".equalsIgnoreCase(account.getAccountType())) {
                        AccountDto accountDto = new FixedDepositAccountDto();
                        BeanUtils.copyProperties(account, accountDto);
                        accountDtos.add(accountDto);
                    }
                }
                request.setAccounts(accountDtos);
            }
            if (!CollectionUtils.isEmpty(model.getAddresses())) {
                List<AddressDto> addressDtos = new ArrayList<>();
                for (Address address : model.getAddresses()) {
                    AddressDto addressDto = new AddressDto();
                    BeanUtils.copyProperties(addressDto, address);
                    addressDtos.add(addressDto);
                }
                request.setAddresses(addressDtos);
            }
        }
        return request;
    }
}
