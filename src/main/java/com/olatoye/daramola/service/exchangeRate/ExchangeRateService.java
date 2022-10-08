package com.olatoye.daramola.service.exchangeRate;

import com.olatoye.daramola.model.dto.ExchangeRateResponseDto;
import com.olatoye.daramola.model.entity.ExchangeRate;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface ExchangeRateService {
    CompletableFuture<ExchangeRateResponseDto> getLatestExchangeRate();
    CompletableFuture<Set<ExchangeRateResponseDto>> getExchangeRatesBetweenDates(String from, String to);

//    INTERNAL
    CompletableFuture<Set<ExchangeRateResponseDto>> getAllExchangeRatesInDb();
    CompletableFuture<Set<ExchangeRateResponseDto>> getExchangeRatesInDbBetweenDates(String from, String to);
}
