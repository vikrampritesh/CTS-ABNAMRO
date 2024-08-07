package com.abnamro.personalbanking.interestremainder.domains;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDto {
    @Size(max = 50, message = "streetAddress1 characters max size is 50")
    private String streetAddress1;
    @Size(max = 50, message = "streetAddress2 characters max size is 50")
    private String streetAddress2;
    @Size(max = 50, message = "state characters max size is 50")
    private String state;
    @Size(max = 50, message = "city characters max size is 50")
    private String city;
    @Size(max = 5, message = "zip characters max size is 5")
    private String zip;
}