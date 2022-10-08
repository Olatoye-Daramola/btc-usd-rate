package com.olatoye.daramola.utils.config;

import com.olatoye.daramola.model.dto.ExchangeRateResponseDto;
import com.olatoye.daramola.model.entity.ExchangeRate;
import com.olatoye.daramola.utils.exception.ExchangeRateNotMappedException;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

    public ExchangeRateResponseDto map(ExchangeRate exchangeRate) {
        try {
            return ExchangeRateResponseDto.builder()
                    .btcValue(exchangeRate.getBtcValue().toString())
                    .usdValue(exchangeRate.getUsdValue().toString())
                    .dateCreated(exchangeRate.getDateCreated().toString())
                    .build();
        } catch (Exception e) {
            throw new ExchangeRateNotMappedException(e.getMessage());
        }
    }
}
