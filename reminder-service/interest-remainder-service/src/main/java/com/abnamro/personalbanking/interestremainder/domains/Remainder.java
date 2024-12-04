package com.abnamro.personalbanking.interestremainder.domains;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Remainder {

        String id;

        @Size(max=50, message="'remainderText' not allowed more than 50 characters")
        String remainderText;

        String status;

        Integer repeatedTimes;

        @NotBlank(message = "accountType is missing, , possible values are FIXED/SAVINGS/CHECKING")
        @Size(max=25, message="'accountType' not allowed more than 25 characters")
        String accountType;
}
