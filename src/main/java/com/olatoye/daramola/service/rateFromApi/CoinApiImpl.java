package com.olatoye.daramola.service.rateFromApi;

import com.olatoye.daramola.model.entity.ExchangeRate;
import com.olatoye.daramola.utils.exception.exchangeRateExceptions.ExchangeRateNotFoundException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service("coinApi")
@Profile("dev")
public class CoinApiImpl implements RateExtractorService {

    @Value("${COIN_API_URL}")
    private String baseUrl;

    @Value("${COIN_API_KEY}")
    private String apiKey;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            .withZone(ZoneId.of("UTC"));

    @Override
    public ExchangeRate getLatestRate() {
        try {
            String res = callApi(baseUrl);

            return parseJsonToExchangeRateObject(res);
        } catch (Exception e) {
            throw new ExchangeRateNotFoundException(e.getMessage());
        }
    }

    @Override
    public Set<ExchangeRate> getSetOfExchangeRates(String from, String to) {
        try {
            String url = baseUrl + "/history?period_id=1MIN&time_start=" + from + "&time_end=" + to;
            String res = callApi(url);

            return parseJsonToExchangeRateArray(res);
        } catch (Exception e) {
            throw new ExchangeRateNotFoundException(e.getMessage());
        }
    }


    /*
    * --------------------------------     HELPER METHODS    --------------------------------
    */

    @NotNull
    private String callApi(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("X-CoinAPI-Key", apiKey)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        return Objects.requireNonNull(response.body()).string();
    }

    private static ExchangeRate parseJsonToExchangeRateObject(String res) {
        JSONObject jsonObject = new JSONObject(res);
        BigDecimal usdRate = jsonObject.getBigDecimal("rate");
        String dateCreated = jsonObject.getString("time");

        return ExchangeRate.builder()
                .btcValue(BigDecimal.valueOf(1))
                .usdValue(usdRate)
                .dateCreated(LocalDateTime.parse(dateCreated.split("\\.")[0]))
                .build();
    }

    private Set<ExchangeRate> parseJsonToExchangeRateArray(String res) {
        JSONArray jsonArray = new JSONArray(res);
        Set<ExchangeRate> exchangeRateArray = new LinkedHashSet<>();

        jsonArray.forEach(jsonObject -> {
            JSONObject newJsonObject = new JSONObject(jsonObject.toString());
            BigDecimal currentRate = newJsonObject.getBigDecimal("rate_close");
            String dateCreated = newJsonObject.getString("time_close");
            exchangeRateArray.add(ExchangeRate.builder()
                    .btcValue(BigDecimal.valueOf(1))
                    .usdValue(currentRate)
                    .dateCreated(LocalDateTime.parse(dateCreated.split("\\.")[0], formatter))
                    .build());
        });
        return exchangeRateArray;
    }
}
