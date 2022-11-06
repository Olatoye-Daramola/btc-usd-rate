package com.olatoye.daramola.web.functionalRoute.handler;

import com.olatoye.daramola.model.dto.ExchangeRateResponseDto;
import com.olatoye.daramola.service.exchangeRate.ExchangeRateService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component
public class ExchangeRateHandler {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateHandler(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    public ServerResponse getLatestRate(ServerRequest request) {
        CompletableFuture<ExchangeRateResponseDto> dtoResponse= exchangeRateService.getLatestExchangeRate();
        return ServerResponse.ok().body(dtoResponse);
    }

    public ServerResponse getRateBetweenDate(ServerRequest request) {
        String from = request.param("from").orElse(null);
        String to = request.param("to").orElse(null);
        CompletableFuture<Set<ExchangeRateResponseDto>> dtoResponse =
                exchangeRateService.getExchangeRatesBetweenDates(from, to);
        return ServerResponse.ok().body(dtoResponse);
    }
}
