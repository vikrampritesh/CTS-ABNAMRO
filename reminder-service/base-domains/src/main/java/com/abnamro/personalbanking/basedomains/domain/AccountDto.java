package com.abnamro.personalbanking.basedomains.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.micrometer.common.util.StringUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Data
@SuperBuilder
@AllArgsConstructor
@ToString
@NoArgsConstructor
@JsonDeserialize(as = FixedDepositAccountDto.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Validated
public abstract class AccountDto {

     /**
      * Primary Key
      */
     Long id;

     /**
      * accountNumber of the Customer
      */
     String accountNumber;

     /**
      * principalAmount of the Customer
      */
     @NotBlank (message = "principalAmount is missing")
     Double principalAmount;

     /**
      * interestRate of the Customer
      */
     @NotBlank (message = "interestRate is missing")
     Double interestRate;

     /**
      * interestAmount of the Customer after interest calculation
      */
     Double interestAmount;

     /**
      * balance of the Customer
      */
     Double balance;

     /**
      * dateOfOpening of the Customer
      */
     //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
     @NotBlank (message = "dateOfOpening is missing")
     @JsonFormat(pattern = "yyyy-MM-dd")
     String dateOfOpening;

     /**
      * dateOfClosing of the Customer
      */
     //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
     @NotBlank (message = "dateOfClosing is missing")
     @JsonFormat(pattern = "yyyy-MM-dd")
     String dateOfClosing;

     /**
      * status of the Customer whether Active/Dormant
      */
     String status;

     /**
      * accountType of the Customer
      */
     @NotBlank(message = "accountType is missing")
     @Size(max = 25, message = "accountType characters max size is 25")
     String accountType;

     /**
      * isSeniorCitizen of the Customer
      */
     Boolean isSeniorCitizen;

     /**
      * Time Period in months of holding funds
      */
     Long months;

     /**
      * Time Period in years of holding funds
      */
     Long years;

     @JsonIgnore
     public Long getMonthsCountAsOfNow() {
          if (!StringUtils.isBlank(dateOfOpening)) {
               DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd");
               return ChronoUnit.MONTHS.between(YearMonth.from(LocalDate.parse(dateOfOpening, parser)),
                       YearMonth.from(LocalDate.parse(LocalDate.now().toString())));
          }
          return null;
     }

     @JsonIgnore
     public Long getYearsCountAsofNow() {
          if (!StringUtils.isBlank(dateOfOpening)) {
               DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd");
               return ChronoUnit.YEARS.between(YearMonth.from(LocalDate.parse(dateOfOpening, parser)),
                                               YearMonth.from(LocalDate.parse(LocalDate.now().toString(), parser)));
          }
          return null;
     }

     @JsonIgnore
     final Double calcIntrestByMonth() {
          months = getMonthsCountAsOfNow();
          if (principalAmount != null && months != null && interestRate != null) {
               interestAmount = (principalAmount * months * (interestRate / 12)) / 100;
               interestAmount = new BigDecimal(interestAmount).setScale(2, RoundingMode.HALF_UP).doubleValue();
               balance = interestAmount + principalAmount;
          }
          return  interestAmount;
     }

     @JsonIgnore
     final Double calcIntrestByYear() {
          years = getYearsCountAsofNow();
          if (principalAmount != null && years != null && interestRate != null) {
               interestAmount = (principalAmount * years * interestRate) / 100;
               interestAmount = new BigDecimal(interestAmount).setScale(2, RoundingMode.HALF_UP).doubleValue();
               balance = interestAmount + principalAmount;
          }
          return interestAmount;
     }

     /**
      * @param amt
      */
     abstract void deposit(Double amt);

     /**
      * @param amt
      */
     abstract void withdraw(Double amt);
}
