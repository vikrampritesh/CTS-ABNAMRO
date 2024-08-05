package com.abnamro.personalbanking.base_domains;

import com.abnamro.personalbanking.basedomains.domain.FixedDepositAccountDto;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class BaseDomainsApplicationTests {

	@Test
	void testFixedDepositAccountIsMaturingDepositYes() {
		LocalDate dateOfOpening = LocalDate.of(2019, Month.AUGUST, 15);

		FixedDepositAccountDto fixedDepositAccount = FixedDepositAccountDto.builder().accountNumber("123445L").
													principalAmount(1000.0).
													interestRate(10.0).dateOfOpening(dateOfOpening.toString()).
													maturityDate(LocalDate.of(2024, Month.AUGUST, 15).toString()).
													build();
		assertEquals(fixedDepositAccount.isMaturingDepositWithInMonth(3), true);
	}

	@Test
	void testFixedDepositAccountIsMaturingDepositNo() {
		LocalDate dateOfOpening = LocalDate.of(2019, Month.AUGUST, 15);

		FixedDepositAccountDto fixedDepositAccount = FixedDepositAccountDto.builder().accountNumber("123445L").
				principalAmount(1000.0).
				interestRate(10.0).dateOfOpening(dateOfOpening.toString()).
				maturityDate(LocalDate.of(2024, Month.JANUARY, 15).toString()).
				build();
		assertEquals(fixedDepositAccount.isMaturingDepositWithInMonth(1), false);
	}

	@Test
	void testMaturityAmountValid() {
		LocalDate dateOfOpening = LocalDate.of(2019, Month.AUGUST, 1);

		FixedDepositAccountDto fixedDepositAccount = FixedDepositAccountDto.builder().accountNumber("123445L").
													principalAmount(1000.0).
													interestRate(10.0).dateOfOpening(dateOfOpening.toString()).
													build();
		Double maturityAmount = fixedDepositAccount.getMaturityAmount();
		assertNotNull(maturityAmount);
		assertEquals(maturityAmount, 1500);
	}

	@Test
	void testMaturityAmountInvalid() {
		LocalDate dateOfOpening = LocalDate.of(2019, Month.AUGUST, 1);

		FixedDepositAccountDto fixedDepositAccount = FixedDepositAccountDto.builder().accountNumber("123445L").
				principalAmount(2000.0).
				interestRate(10.0).dateOfOpening(dateOfOpening.toString()).
				build();
		Double maturityAmount = fixedDepositAccount.getMaturityAmount();
		assertNotNull(maturityAmount);
		assertNotEquals(maturityAmount, 1500);
	}

	@Test
	void testFixedDepositAccountIsMaturingDepositYes2() {
		LocalDate dateOfOpening = LocalDate.of(2019, Month.AUGUST, 5);
		LocalDate dateOfMaturing = LocalDate.of(2024, Month.AUGUST, 6);

		FixedDepositAccountDto fixedDepositAccount = FixedDepositAccountDto.builder().accountNumber("123445L").
				principalAmount(1000.0).
				maturityDate(dateOfMaturing.toString()).
				interestRate(10.0).dateOfOpening(dateOfOpening.toString()).
				//maturityDate(LocalDate.of(2024, Month.AUGUST, 15).toString()).
				build();
		assertEquals(fixedDepositAccount.isMaturingDepositWithInMonth(0), true);
	}

}
