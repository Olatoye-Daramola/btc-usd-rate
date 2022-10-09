package com.olatoye.daramola.service.exchangeRate;

import com.olatoye.daramola.model.dto.CronDto;
import com.olatoye.daramola.model.dto.ExchangeRateResponseDto;
import com.olatoye.daramola.model.entity.ExchangeRate;
import com.olatoye.daramola.repository.ExchangeRateRepository;
import com.olatoye.daramola.service.rateFromApi.RateExtractorService;
import com.olatoye.daramola.utils.config.ModelMapper;
import com.olatoye.daramola.utils.exception.exchangeRateExceptions.ExchangeRateNotFoundException;
import com.olatoye.daramola.utils.exception.exchangeRateExceptions.ExchangeRateNotSavedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final RateExtractorService rateExtractorService;
    private final ModelMapper modelMapper;
    private final Executor executor;

    @Value("${cronValue")
    String cronValue;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("+yyyy-MM-dd'T'HH:mm:ss")
            .withZone(ZoneId.of("UTC"));

    public ExchangeRateServiceImpl(ExchangeRateRepository exchangeRateRepository,
                                   @Qualifier("coinApi") RateExtractorService rateExtractorService,
                                   ModelMapper modelMapper, @Qualifier("taskExecutor") Executor executor) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.rateExtractorService = rateExtractorService;
        this.modelMapper = modelMapper;
        this.executor = executor;
    }

    @Override
    public CompletableFuture<ExchangeRateResponseDto> getLatestExchangeRate() {
        try {
            return CompletableFuture.supplyAsync(() -> {
                ExchangeRate exchangeRate = exchangeRateRepository
                        .findByDateCreated(LocalDateTime.now())
                        .orElse(saveLatestExchangeRateToDb());
                return modelMapper.map(exchangeRate);
            }, executor);
        } catch (Exception e) {
            throw new ExchangeRateNotFoundException("Latest Exchange rate not found because: " + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<Set<ExchangeRateResponseDto>> getExchangeRatesBetweenDates(String from, String to) {
        try {
            return CompletableFuture.supplyAsync(() ->
                            rateExtractorService.getSetOfExchangeRates(from, to)
                                    .stream()
                                    .map(rate -> exchangeRateRepository.findByDateCreated(rate.getDateCreated())
                                            .orElse(exchangeRateRepository.save(rate)))
                                    .map(modelMapper::map)
                                    .collect(Collectors.toSet())
                    , executor);
        } catch (Exception e) {
            throw new ExchangeRateNotFoundException("Exchange rates not found because: " + e.getMessage());
        }
    }

    /*
     * --------------------------------     INTERNAL METHODS    --------------------------------
     */

    @Override
    public CompletableFuture<Set<ExchangeRateResponseDto>> getAllExchangeRatesInDb() {
        try {
            return CompletableFuture.supplyAsync(() ->
                            exchangeRateRepository.findAll()
                                    .stream()
                                    .map(modelMapper::map)
                                    .collect(Collectors.toSet()),
                    executor);
        } catch (Exception e) {
            throw new ExchangeRateNotFoundException("Exchange rates not found because: " + e.getMessage());
        }
    }

    @Override
    public String changeExchangeRateCheckPeriodUsingCronExp(CronDto newCronExpression) {
        if (newCronExpression != null) {
            this.cronValue = newCronExpression.toString();
            return "Update time successfully changed";
        }
        return "Update time not changed because cron expression is null";
    }

    @Scheduled(cron = "${cronValue}")
    @Override
    public void updateDbWithLatestExchangeRate() {
        ExchangeRate exchangeRate = rateExtractorService.getLatestRate();
        if (exchangeRate != null) exchangeRateRepository.save(exchangeRate);
    }

    /*
     * --------------------------------     HELPER METHODS    --------------------------------
     */

    public ExchangeRate saveLatestExchangeRateToDb() {
        try {
            ExchangeRate exchangeRate = rateExtractorService.getLatestRate();
            if (exchangeRate != null) return exchangeRateRepository.save(exchangeRate);
            throw new ExchangeRateNotSavedException("Latest exchange rate not pulled");
        } catch (Exception e) {
            throw new ExchangeRateNotSavedException("Latest exchange rate not saved because: " + e.getMessage());
        }
    }
}
