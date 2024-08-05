package com.abnamro.personalbanking.basedomains.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class CheckingAccountDto extends AccountDto {

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
}
