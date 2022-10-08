package com.olatoye.daramola.service.rateFromApi;

import com.olatoye.daramola.model.entity.ExchangeRate;
import com.olatoye.daramola.utils.exception.ExchangeRateNotFoundException;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Service("coinDesk")
public class CoinDeskImpl implements RateExtractorService {

    @Value("${COIN_DESK_URL}")
    private String url;

    private ExchangeRate exchangeRate;

    @Override
    public ExchangeRate getLatestRate() {
        OkHttpClient client = new OkHttpClient();

        Request getRequest = new Request.Builder()
                .url(url)
                .build();

        client.newCall(getRequest).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String str = Objects.requireNonNull(response.body()).string();
                exchangeRate = parseBitcoinPrice(str);
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                throw new ExchangeRateNotFoundException(e.getMessage());
            }
        });
        return exchangeRate != null ? exchangeRate : ExchangeRate.builder().build();
    }

    @Override
    public Set<ExchangeRate> getSetOfExchangeRates(String from, String to) {
        return null;
    }

    private ExchangeRate parseBitcoinPrice(String body) {
        JSONObject jsonObject = new JSONObject(body);
        BigDecimal currentRate = jsonObject.getJSONObject("bpi")
                .getJSONObject("USD")
                .getBigDecimal("rate_float");
        String dateCreated = jsonObject.getJSONObject("time")
                .getString("updatedISO");

        return ExchangeRate.builder()
                .usdValue(currentRate)
                .dateCreated(LocalDateTime.parse(dateCreated))
                .build();
    }
}
