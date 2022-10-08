package com.olatoye.daramola.model.dto;

import lombok.Builder;

@Builder
public class ExchangeRateResponseDto {

    private String btcValue;
    private String usdValue;
    private String dateCreated;
}
