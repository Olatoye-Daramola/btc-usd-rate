package com.olatoye.daramola.service.exchangeRate;

import com.olatoye.daramola.model.dto.ExchangeRateResponseDto;
import com.olatoye.daramola.model.entity.ExchangeRate;
import com.olatoye.daramola.repository.ExchangeRateRepository;
import com.olatoye.daramola.service.rateFromApi.RateExtractorService;
import com.olatoye.daramola.utils.config.ModelMapper;
import com.olatoye.daramola.utils.exception.ExchangeRateException;
import com.olatoye.daramola.utils.exception.ExchangeRateNotFoundException;
import com.olatoye.daramola.utils.exception.ExchangeRateNotSavedException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final RateExtractorService rateExtractorService;
    private final ModelMapper modelMapper;
    private final Executor executor;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("+yyyy-MM-dd'T'HH:mm:ss'Z'")
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
        return CompletableFuture.supplyAsync(() -> {
            ExchangeRate latestExchangeRateInDb = exchangeRateRepository.findTopByOrderByDateCreatedDesc();
            ExchangeRate exchangeRate =
                    latestExchangeRateInDb.getDateCreated().getMinute() - LocalDateTime.now().getMinute() == 0
                            ? latestExchangeRateInDb
                            : saveLatestExchangeRateToDb();
            return modelMapper.map(exchangeRate);
        }, executor);
    }

    @Override
    public CompletableFuture<Set<ExchangeRateResponseDto>> getExchangeRatesBetweenDates(String from, String to) {
        return CompletableFuture.supplyAsync(() ->
                        rateExtractorService.getSetOfExchangeRates(from, to)
                                .stream()
                                .map(rate -> exchangeRateRepository.findByDateCreated(rate.getDateCreated())
                                        .orElse(exchangeRateRepository.save(rate)))
                                .map(modelMapper::map)
                                .collect(Collectors.toSet())
                , executor);
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
            throw new ExchangeRateNotFoundException(e.getMessage());
        }
    }

    @Override
    public CompletableFuture<Set<ExchangeRateResponseDto>> getExchangeRatesInDbBetweenDates(String from, String to) {
        try {
            return CompletableFuture.supplyAsync(() ->
                            exchangeRateRepository.findByDateCreatedBetween(LocalDateTime.parse(from, formatter),
                                            LocalDateTime.parse(to, formatter))
                                    .stream()
                                    .map(modelMapper::map)
                                    .collect(Collectors.toSet()),
                    executor);
        } catch (Exception e) {
            throw new ExchangeRateNotFoundException(e.getMessage());
        }
    }



    /*
     * --------------------------------     HELPER METHODS    --------------------------------
     */

    public ExchangeRate saveLatestExchangeRateToDb() {
        try {
            ExchangeRate exchangeRate = rateExtractorService.getLatestRate();
            if (exchangeRate != null) return exchangeRateRepository.save(exchangeRate);
            throw new ExchangeRateNotSavedException("Latest exchange rate not saved");
        } catch (Exception e) {
            throw new ExchangeRateNotSavedException("Latest exchange rate not pulled");
        }
    }
}
