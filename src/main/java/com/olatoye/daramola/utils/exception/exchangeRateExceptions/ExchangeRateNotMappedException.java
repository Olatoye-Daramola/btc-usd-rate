package com.olatoye.daramola.utils.exception.exchangeRateExceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class ExchangeRateNotMappedException extends ExchangeRateException{
    public ExchangeRateNotMappedException(String message) {
        super(message);
    }
}
