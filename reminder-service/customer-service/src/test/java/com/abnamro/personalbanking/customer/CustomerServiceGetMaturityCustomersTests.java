package com.abnamro.personalbanking.customer;

import com.abnamro.personalbanking.customer.domain.AccountDto;
import com.abnamro.personalbanking.customer.domain.AddressDto;
import com.abnamro.personalbanking.customer.domain.CustomerRequest;
import com.abnamro.personalbanking.customer.domain.FixedDepositAccountDto;
import com.abnamro.personalbanking.customer.enums.AccountStatus;
import com.abnamro.personalbanking.customer.enums.AccountType;
import com.abnamro.personalbanking.customer.model.Account;
import com.abnamro.personalbanking.customer.model.Address;
import com.abnamro.personalbanking.customer.model.Customer;
import com.abnamro.personalbanking.customer.model.FixedDepositAccount;
import com.abnamro.personalbanking.customer.repo.CustomerRepository;
import com.abnamro.personalbanking.customer.service.AccountsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.BeanUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.Month;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class CustomerServiceGetMaturityCustomersTests {

    @Mock
    private AccountsServiceImpl accountsService;

    @Mock
    private CustomerRepository customerRepository;

    @Test
    void testGetCurrentMonthMaturityDoneCustomers() {
        List<CustomerRequest> customers = new ArrayList<>();
        List<Customer> customerModels = new ArrayList<>();

        customers.add(CustomerRequest.builder().id(12345L).email("kishorebabu.d@cognizant.com").
                lastName("Diyyana").firstName("Kishore").phoneNumber("91-7660954321").
                addresses(List.of(AddressDto.builder().streetAddress1("3-22, Hukkumpet").city("Rajahmundry").state("AP").build())).
                accounts(List.of(FixedDepositAccountDto.builder().accountNumber("123445L").
                        principalAmount(1000.0).
                        accountType(AccountType.FIXED.name()).
                        interestRate(10.0).dateOfOpening(LocalDate.of(2019, Month.AUGUST, 15).toString()).
                        maturityDate(LocalDate.of(2024, Month.AUGUST, 15).toString()).
                        build())).build());
        for (CustomerRequest customerRequest:customers) {
            Customer model = new Customer();
            buildtoModel(model, customerRequest);
            customerModels.add(model);
        }
        when(customerRepository.findAll()).thenReturn(customerModels);
        when(accountsService.getMaturityCustomers(0)).thenReturn(customers);
        List<CustomerRequest> response = accountsService.getMaturityCustomers(0);
        Assertions.assertNotNull(response);
        assertEquals(customers, response);
    }

    @Test
    void testGetCurrentMonthMaturityDoneCustomersNegative1DiffYear() {
        List<CustomerRequest> customers = new ArrayList<>();
        List<Customer> customerModels = new ArrayList<>();

        customers.add(CustomerRequest.builder().id(12345L).email("kishorebabu.d@cognizant.com").
                lastName("Diyyana").firstName("Kishore").phoneNumber("91-7660954321").
                addresses(List.of(AddressDto.builder().streetAddress1("3-22, Hukkumpet").city("Rajahmundry").state("AP").build())).
                accounts(List.of(FixedDepositAccountDto.builder().accountNumber("123445L").
                        principalAmount(1000.0).
                        accountType(AccountType.FIXED.name()).
                        interestRate(10.0).dateOfOpening(LocalDate.of(2018, Month.AUGUST, 15).toString()).
                        maturityDate(LocalDate.of(2024, Month.AUGUST, 15).toString()).
                        build())).build());
        for (CustomerRequest customerRequest:customers) {
            Customer model = new Customer();
            buildtoModel(model, customerRequest);
            customerModels.add(model);
        }
       // when(customerRepository.findAll()).thenReturn(customerModels);
        when(accountsService.getAll()).thenReturn(customers);
        when(accountsService.getMaturityCustomers(0)).thenReturn(customers);
        List<CustomerRequest> response = accountsService.getMaturityCustomers(2);
        Assertions.assertNotNull(response);
        assertNotEquals(customers, response);
    }

    @Test
    void testGetCurrentMonthMaturityDoneCustomersWhenNoData() {
        List<CustomerRequest> customers = new ArrayList<>();
        List<Customer> customerModels = new ArrayList<>();

        customers.add(null);
        for (CustomerRequest customerRequest:customers) {
            Customer model = new Customer();
            buildtoModel(model, customerRequest);
            customerModels.add(model);
        }
        when(customerRepository.findAll()).thenReturn(customerModels);
        when(accountsService.getMaturityCustomers(0)).thenReturn(customers);
        List<CustomerRequest> response = accountsService.getMaturityCustomers(0);
        Assertions.assertNotNull(response);
        Assertions.assertEquals((response.isEmpty()), customers.isEmpty());
    }

    private void buildtoModel(Customer model, CustomerRequest request) {
        if (model != null && request != null) {
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
                List<Address> addressModels = new ArrayList<>();
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
    }

}
