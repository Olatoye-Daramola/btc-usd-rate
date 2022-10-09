package com.olatoye.daramola.controller;

import com.olatoye.daramola.model.dto.ExchangeRateResponseDto;
import com.olatoye.daramola.service.exchangeRate.ExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/rates")
@Slf4j
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getLatestExchangeRate() {
        CompletableFuture<ExchangeRateResponseDto> responseDto = exchangeRateService.getLatestExchangeRate();
        return ResponseEntity.ok().body(responseDto.join());
    }

    @GetMapping("/period")
    public ResponseEntity<?> getExchangeRatesBetweenDates(@RequestParam("from") String from,
                                                          @RequestParam("to") String to) {
        CompletableFuture<Set<ExchangeRateResponseDto>> responseDto = exchangeRateService.getExchangeRatesBetweenDates(from, to);
        return ResponseEntity.ok().body(responseDto.join());
    }
}
