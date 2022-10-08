package com.olatoye.daramola.service.rateFromApi;

import com.olatoye.daramola.model.entity.ExchangeRate;

import java.math.BigDecimal;
import java.util.Set;

public interface RateExtractorService {
    ExchangeRate getLatestRate();
    Set<ExchangeRate> getSetOfExchangeRates(String from, String to);
}
