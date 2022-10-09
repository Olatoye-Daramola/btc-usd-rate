package com.olatoye.daramola.service.exchangeRate;

import com.olatoye.daramola.model.dto.CronDto;
import com.olatoye.daramola.model.dto.ExchangeRateResponseDto;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface ExchangeRateService {
    CompletableFuture<ExchangeRateResponseDto> getLatestExchangeRate();
    CompletableFuture<Set<ExchangeRateResponseDto>> getExchangeRatesBetweenDates(String from, String to);

//    INTERNAL
    CompletableFuture<Set<ExchangeRateResponseDto>> getAllExchangeRatesInDb();
    String changeExchangeRateCheckPeriodUsingCronExp(CronDto newCronExpression);
    void updateDbWithLatestExchangeRate();
}
