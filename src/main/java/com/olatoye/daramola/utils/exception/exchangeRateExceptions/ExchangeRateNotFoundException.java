package com.olatoye.daramola.utils.exception.exchangeRateExceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(NOT_FOUND)
public class ExchangeRateNotFoundException extends ExchangeRateException{
    public ExchangeRateNotFoundException(String message) {
        super(message);
    }
}
