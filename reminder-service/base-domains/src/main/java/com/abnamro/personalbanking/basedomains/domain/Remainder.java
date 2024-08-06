package com.abnamro.personalbanking.basedomains.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Remainder {

        String id;

        @Size(max=50, message="'remainderText' not allowed more than 50 characters")
        String remainderText;

        String status;

        Integer repeatedTimes;

        @NotBlank(message = "typeOfAccount is missing")
        @Size(max=25, message="'typeOfAccount' not allowed more than 25 characters")
        String typeOfAccount;
}
