package com.olatoye.daramola.service.rateFromApi;

import com.olatoye.daramola.model.entity.ExchangeRate;
import com.olatoye.daramola.utils.exception.ExchangeRateNotFoundException;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Service("coinApi")
public class CoinApiImpl implements RateExtractorService {

    @Value("${COIN_API_URL}")
    private String baseUrl;

    @Value("${COIN_API_KEY}")
    private String apiKey;

    private ExchangeRate latestExchangeRate;
    private Set<ExchangeRate> setOfExchangeRates;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("+yyyy-MM-dd'T'HH:mm:ss'Z'")
            .withZone(ZoneId.of("UTC"));

    @Override
    public ExchangeRate getLatestRate() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(baseUrl)
                    .addHeader("Accept", "application/json")
                    .addHeader("X-CoinAPI-Key", apiKey)
                    .build();
            pullLatestRateAsync(client, request);
            return latestExchangeRate != null ? latestExchangeRate : ExchangeRate.builder().build();
        } catch (Exception e) {
            throw new ExchangeRateNotFoundException(e.getMessage());
        }
    }

    @Override
    public Set<ExchangeRate> getSetOfExchangeRates(String from, String to) {
        try {
            OkHttpClient client = new OkHttpClient();
            String url = baseUrl + "history?period_id=1MIN&time_start=" + from + "&time_end=" + to;
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json")
                    .addHeader("X-CoinAPI-Key", apiKey)
                    .build();
            pullRatesBetweenDatesAsync(client, request);
            return !setOfExchangeRates.isEmpty() ? setOfExchangeRates : new LinkedHashSet<>();
        } catch (Exception e) {
            throw new ExchangeRateNotFoundException(e.getMessage());
        }
    }




    /*
     * --------------------------------     HELPER METHODS    --------------------------------
    */

    private void pullLatestRateAsync(OkHttpClient client, Request getRequest) {
        client.newCall(getRequest).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String str = Objects.requireNonNull(response.body()).string();
                latestExchangeRate = extractLatestRate(str);
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                throw new ExchangeRateNotFoundException(e.getMessage());
            }
        });
    }

    private void pullRatesBetweenDatesAsync(OkHttpClient client, Request getRequest) {
        client.newCall(getRequest).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String str = Objects.requireNonNull(response.body()).string();
                setOfExchangeRates = extractRatesBetweenPeriods(str);
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                throw new ExchangeRateNotFoundException(e.getMessage());
            }
        });
    }

    private ExchangeRate extractLatestRate(String body) {
        JSONObject jsonObject = new JSONObject(body);
        BigDecimal currentRate = jsonObject.getBigDecimal("rate");
        String dateCreated = jsonObject.getString("time");

        return ExchangeRate.builder()
                .usdValue(currentRate)
                .dateCreated(LocalDateTime.parse(dateCreated, formatter))
                .build();
    }

    private Set<ExchangeRate> extractRatesBetweenPeriods(String body) {
        Set<ExchangeRate> rates = new LinkedHashSet<>();
        JSONArray jsonArray = new JSONArray(body);
        jsonArray.forEach(jsonObject -> {
            JSONObject newJsonObject = new JSONObject(jsonObject);
            BigDecimal currentRate = newJsonObject.getBigDecimal("rate");
            String dateCreated = newJsonObject.getString("time");
            rates.add(ExchangeRate.builder()
                    .usdValue(currentRate)
                    .dateCreated(LocalDateTime.parse(dateCreated, formatter))
                    .build());
        });

        return rates;
    }
}
