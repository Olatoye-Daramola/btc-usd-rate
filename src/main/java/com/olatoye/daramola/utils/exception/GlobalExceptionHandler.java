package com.olatoye.daramola.utils.exception;

import com.olatoye.daramola.utils.exception.exchangeRateExceptions.ExchangeRateException;
import com.olatoye.daramola.utils.exception.exchangeRateExceptions.ExchangeRateNotFoundException;
import com.olatoye.daramola.utils.exception.exchangeRateExceptions.ExchangeRateNotMappedException;
import com.olatoye.daramola.utils.exception.exchangeRateExceptions.ExchangeRateNotSavedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ExchangeRateNotFoundException.class})
    public ResponseEntity<Object> notFoundException(ExchangeRateNotFoundException ex) {
        String message = ex.getMessage();
        return new ResponseEntity<>(message, NOT_FOUND);
    }

    @ExceptionHandler({ExchangeRateNotMappedException.class, ExchangeRateNotSavedException.class})
    public ResponseEntity<Object> internalServerError(ExchangeRateException ex) {
        String message = ex.getMessage();
        return new ResponseEntity<>(message, INTERNAL_SERVER_ERROR);
    }
}
