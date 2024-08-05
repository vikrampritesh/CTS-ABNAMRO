package com.abnamro.personalbanking.basedomains.exception;

import com.abnamro.personalbanking.basedomains.domain.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Response> handleRequestConflict(MethodArgumentNotValidException ex, WebRequest request) {
        String errMsg = ex.getBindingResult().getFieldErrors().stream().
                map((DefaultMessageSourceResolvable::getDefaultMessage)).toList().toString();
        LOGGER.error(errMsg, ex);
        return new ResponseEntity<>(Response.builder().
                statusCode(HttpStatus.BAD_REQUEST.value())
               .statusName(HttpStatus.BAD_REQUEST.name()).message(errMsg).build(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
	public ResponseEntity<Response> handleMethodException(Exception ex, WebRequest request) {
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(Response.builder().
                statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
               .statusName(HttpStatus.INTERNAL_SERVER_ERROR.name()).message(ex.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
}