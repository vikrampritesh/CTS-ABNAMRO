package com.abnamro.personalbanking.emailservice.domains;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@EqualsAndHashCode
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Validated
public class CustomerRequest {
    /**
     * Primary Key
     */
    Long id;

    /**
     * firstName of the Bank Customer
     */
    @NotBlank(message = "firstName is missing")
    @Size(max = 100, message = "firstName characters max size is 100")
    String firstName;

    /**
     * lastName of the Bank Customer
     */
    @NotBlank (message = "lastName is missing")
    @Size(max = 100, message = "lastName characters max size is 100")
    String lastName;

    /**
     * phoneNumber of the Bank Customer
     */
    String phoneNumber;

    /**
     * email of the Bank Customer
     */
    @Email(message = "email is invalid")
    String email;

    /**
     * accountDtos of the Bank Customer
     */
    @Valid
    List<AccountDto> accounts;

    /**
     * addressDtos of the Bank Customer
     */
    List <AddressDto> addresses;
}
