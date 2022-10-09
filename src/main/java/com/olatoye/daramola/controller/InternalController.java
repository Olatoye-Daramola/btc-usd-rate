package com.olatoye.daramola.controller;

import com.olatoye.daramola.model.dto.CronDto;
import com.olatoye.daramola.model.dto.ExchangeRateResponseDto;
import com.olatoye.daramola.service.exchangeRate.ExchangeRateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/internals")
public class InternalController {

    private final ExchangeRateService exchangeRateService;

    public InternalController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @PostMapping("/cron")
    public ResponseEntity<?> changeCronExpressionForUpdatingExchangeRate(
            @RequestBody CronDto cronExpression) {
        String res = exchangeRateService.changeExchangeRateCheckPeriodUsingCronExp(cronExpression);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllExchangeRatesInDb() {
        CompletableFuture<Set<ExchangeRateResponseDto>> response = exchangeRateService.getAllExchangeRatesInDb();
        return ResponseEntity.ok().body(response.join());
    }
}
