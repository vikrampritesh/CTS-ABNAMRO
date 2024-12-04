package com.abnamro.personalbanking.interestremainder.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

/**
 * The Class GenericException is a custom exception class.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
public class BusinessException extends Exception {

    private final String message;
    private final HttpStatus status;
    private final Exception exception;

}
