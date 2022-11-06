package com.olatoye.daramola.service.exchangeRate;

import com.olatoye.daramola.model.dto.ExchangeRateResponseDto;
import com.olatoye.daramola.repository.ExchangeRateRepository;
import com.olatoye.daramola.service.rateFromApi.RateExtractorService;
import com.olatoye.daramola.utils.config.ModelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceImplTest {

    @Mock private ExchangeRateRepository exchangeRateRepository;
    @Mock private RateExtractorService rateExtractorService;
    @Mock private ModelMapper modelMapper;
    @Mock private Executor executor;

    private ExchangeRateService exchangeRateService;

    @BeforeEach
    void setUp() {
        exchangeRateService = new ExchangeRateServiceImpl(exchangeRateRepository, rateExtractorService,
                modelMapper, executor);
    }

    @Test
    void getLatestExchangeRate() {
        CompletableFuture<ExchangeRateResponseDto> responseDto = exchangeRateService.getLatestExchangeRate();
        verify(rateExtractorService, times(1)).getLatestRate();
//        assertNotNull(responseDto.join());
//        assertThat(responseDto.join()).isEqualTo(any(ExchangeRateResponseDto.class));
    }

    @Test
    void getExchangeRatesBetweenDates() {
        CompletableFuture<Set<ExchangeRateResponseDto>> responseDto =
                exchangeRateService.getExchangeRatesBetweenDates("2022-10-07T00:00:00", "2022-10-08T00:00:00");
        verify(rateExtractorService, times(1))
                .getSetOfExchangeRates(any(String.class), any(String.class));
//        assertNotNull(responseDto.join());
//        assertThat(responseDto.join()).isEqualTo(any(Set.class));
    }

    @Test
    void getAllExchangeRatesInDb() {
    }

    @Test
    void changeExchangeRateCheckPeriodUsingCronExp() {
        CompletableFuture<Set<ExchangeRateResponseDto>> responseDto =
                exchangeRateService.getExchangeRatesBetweenDates("2022-10-07T00:00:00", "2022-10-08T00:00:00");
        verify(rateExtractorService, times(1))
                .getSetOfExchangeRates(any(String.class), any(String.class));
    }
}