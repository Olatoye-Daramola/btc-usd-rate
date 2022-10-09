package com.olatoye.daramola.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@ToString
public class ExchangeRateResponseDto {

    private String btcValue;
    private String usdValue;
    private String dateCreated;
}
