package com.abnamro.personalbanking.interestremainder;

import com.abnamro.personalbanking.interestremainder.controller.ReminderController;
import com.abnamro.personalbanking.interestremainder.domains.*;
import com.abnamro.personalbanking.interestremainder.enums.AccountType;
import com.abnamro.personalbanking.interestremainder.exception.BusinessException;
import com.abnamro.personalbanking.interestremainder.utils.CommonConstants;
import com.abnamro.personalbanking.interestremainder.utils.CommonUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
class InterestRemainderServiceApplicationTests {

	@InjectMocks
	private ReminderController reminderController;

	MockedStatic<CommonUtils> utilities;

	@BeforeEach
	void setUp() {
		utilities = Mockito.mockStatic(CommonUtils.class);
	}

	@AfterEach
	public void afterTest() {
		utilities.close();
	}

	@Test
	void testPlaceEventReminder() throws Exception {
		List<CustomerRequest> customers = new ArrayList<>();
		Remainder remainder = Remainder.builder().
									accountType(AccountType.FIXED.name()).build();

		Response response = Response.builder().statusName(HttpStatus.OK.name()).
				statusCode(HttpStatus.OK.value()).
				message(CommonConstants.MATURITY_EVENT_SUCCESS_MSG).build();

		Response customerResponse = Response.builder().statusName(HttpStatus.OK.name()).
				statusCode(HttpStatus.OK.value()).
				message("Successfully Retrieved Maturity Customers").build();
				customers.add(CustomerRequest.builder().id(12345L).email("kishorebabu.d@cognizant.com").
						lastName("Diyyana").firstName("Kishore").phoneNumber("91-7660954321").
						accounts(List.of(FixedDepositAccountDto.builder().accountNumber("123445L").
								principalAmount(1000.0).
								accountType(AccountType.FIXED.name()).
								interestRate(10.0).dateOfOpening(LocalDate.of(2019, Month.AUGUST, 15).toString()).
								maturityDate(LocalDate.of(2024, Month.AUGUST, 15).toString()).
								build())).build());
				customerResponse.setData(customers);
		MaturityEvent event = MaturityEvent.builder().
				eventId(UUID.randomUUID().toString()).
				correlationId(UUID.randomUUID().toString()).
				maturityAmount(1000.0).
				dateOfMaturity("2023-03-21").
				customerName("Kishore").
				remainder(remainder).
				build();
		utilities.when(() -> CommonUtils.invokeRestAPI(any())).thenReturn(customerResponse);
		Response actualResponse = reminderController.placeReminder(remainder);
		assertNotNull(actualResponse);
		assertEquals(actualResponse.getStatusCode(),  HttpStatus.OK.value());
	}

	@Test
	void testPlaceEventReminderNotFound() throws Exception {
		Remainder remainder = Remainder.builder().
				accountType(AccountType.FIXED.name()).build();
		Response response = Response.builder().statusName(HttpStatus.OK.name()).
				statusCode(HttpStatus.OK.value()).
				message(CommonConstants.MATURITY_EVENT_SUCCESS_MSG).build();
		Response customerResponse = Response.builder().statusName(HttpStatus.NOT_FOUND.name()).
				statusCode(HttpStatus.NOT_FOUND.value()).
				message("No Maturity Customers Found").build();
		MaturityEvent event = MaturityEvent.builder().
				eventId(UUID.randomUUID().toString()).
				correlationId(UUID.randomUUID().toString()).
				maturityAmount(1000.0).
				dateOfMaturity("2023-03-21").
				customerName("Kishore").
				remainder(remainder).
				build();
		utilities.when(() -> CommonUtils.invokeRestAPI(any())).thenReturn(customerResponse);
		BusinessException ex = assertThrows(BusinessException.class, () -> reminderController.placeReminder(remainder));
		Assertions.assertNotNull(ex);
		assertEquals(ex.getStatus(),  HttpStatus.NOT_FOUND);
	}

	@Test
	void testPlaceEventReminderBadData() throws Exception {
		Remainder remainder = null;
		BusinessException ex = assertThrows(BusinessException.class, () -> reminderController.placeReminder(remainder));
		Assertions.assertNotNull(ex);
		assertEquals(ex.getStatus(),  HttpStatus.BAD_REQUEST);
	}

}
