package com.olatoye.daramola.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString

@Document("rate")
public class ExchangeRate {

    @Id
    private String id;
    private BigDecimal btcValue = BigDecimal.valueOf(1);
    private BigDecimal usdValue;
    private LocalDateTime dateCreated;
}
