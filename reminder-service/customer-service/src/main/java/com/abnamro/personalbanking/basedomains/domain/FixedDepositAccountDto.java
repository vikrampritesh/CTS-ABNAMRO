package com.abnamro.personalbanking.basedomains.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class FixedDepositAccountDto extends AccountDto {

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "maturityDate is missing")
    String maturityDate;

    /**
     * @param amt
     */
    @Override
    void deposit(Double amt) {
        balance = balance+amt;
    }

    /**
     * @param amt
     */
    @Override
    void withdraw(Double amt) {
        balance = balance-amt;
    }

    public Double getBalance() {
        super.calcIntrestByMonth();
        return balance;
    }

    public Double getMaturityAmount() {
        //return balance * Math.pow(1 + interestRate, months);
       this.calcMaturity();
       return balance;
    }

    @JsonIgnore
    public Long getMonthsCountOpeningToMaturity() {
        if (!StringUtils.isBlank(dateOfOpening) && !StringUtils.isBlank(maturityDate)) {
            DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return ChronoUnit.MONTHS.between(YearMonth.from(LocalDate.parse(dateOfOpening, parser)),
                                             YearMonth.from(LocalDate.parse(maturityDate, parser)));
        }
        return null;
    }

    /**
     * This method is used to find the accounts of maturity based on prior months.
     * Eg: Get Maturity Account details by current month, it is priorMonth = 0.
     *     Get Maturity Account details by next month, it is priorMonth = 1.
     *     Get Maturity Account details by after next month, it is priorMonth = 2, so on.
     * @param priorMonth
     * @return boolean
     */
    @JsonIgnore
    public boolean isMaturingDepositWithInMonth(Integer priorMonth) {
       if (maturityDate != null) {
           DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd");
           LocalDate today = LocalDate.parse(LocalDate.now().toString(), parser);
           if (super.getMonthsCountAsOfNow() == (this.getMonthsCountOpeningToMaturity()-priorMonth)) {
               long monthsOfMaturing = ChronoUnit.MONTHS.between(
                       YearMonth.from(today),
                       YearMonth.from(LocalDate.parse(maturityDate, parser)));
               return monthsOfMaturing == priorMonth;
           }
       }
       return false;
    }

    @JsonIgnore
    public Long getYearsCountFromStartToEnd() {
        if (!StringUtils.isBlank(dateOfOpening) && !StringUtils.isBlank(maturityDate)) {
            DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return ChronoUnit.YEARS.between(YearMonth.from(LocalDate.parse(dateOfOpening, parser)),
                    YearMonth.from(LocalDate.parse(maturityDate, parser)));
        }
        return null;
    }

    @JsonIgnore
    final Double calcMaturity() {
        years = getYearsCountFromStartToEnd();
        if (principalAmount != null && years != null && interestRate != null) {
            interestAmount = (principalAmount * years * interestRate) / 100;
            interestAmount = new BigDecimal(interestAmount).setScale(2, RoundingMode.HALF_UP).doubleValue();
            balance = interestAmount + principalAmount;
        }
        return interestAmount;
    }

}
