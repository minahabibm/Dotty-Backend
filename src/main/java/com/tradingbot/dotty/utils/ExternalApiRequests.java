package com.tradingbot.dotty.utils;

import com.tradingbot.dotty.models.dto.ScreenedTickersResponse;
import com.tradingbot.dotty.models.dto.TechnicalIndicatorResponse;
import static com.tradingbot.dotty.utils.LoggingConstants.EXTERNAL_GET_REQUEST_WITH_CRITERIA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Service
public class ExternalApiRequests {

    @Value("${stock-screener-api.base-url}")
    private String baseUrlStockScreenerAPI;
    @Value("${stock-screener-api.APIkey}")
    private String apiKeyStockScreenerAPI;
    @Value("${stock-screener-api.query-params.country}")
    private String country;
    @Value("${stock-screener-api.query-params.market-cap-more-than}")
    private Long marketCapMoreThan;
    @Value("${stock-screener-api.query-params.exchange}")
    private String[] exchange;
    @Value("${stock-screener-api.query-params.beta-more-than}")
    private float betaMoreThan;
    @Value("${stock-screener-api.query-params.is-actively-trading}")
    private boolean isActivelyTrading;

    @Value("${technical-indicators-api.base-url}")
    private String baseUrlTechnicalIndicatorAPI;
    @Value("${technical-indicators-api.APIkey}")
    private String APIkeyTechnicalIndicatorAPI;

    public ScreenedTickersResponse[] stockScreenerUpdateRetrieve() {
        log.info(EXTERNAL_GET_REQUEST_WITH_CRITERIA,  "Stock Screening", "country: " + country + ", market cap more than: " + marketCapMoreThan + ", exchange: " + Arrays.toString(exchange) + ", beta more than " + betaMoreThan + ", and is actively trading.");

//        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder().codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(500 * 1024)).build();
        WebClient webClient = WebClient.builder().baseUrl(baseUrlStockScreenerAPI).build();               //.exchangeStrategies(exchangeStrategies).build();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("country", country)
                        .queryParam("marketCapMoreThan", marketCapMoreThan)
                        .queryParam("exchange", exchange[0])
                        .queryParam("exchange", exchange[1])
                        .queryParam("exchange", exchange[2])
                        .queryParam("betaMoreThan", betaMoreThan)
                        .queryParam("isActivelyTrading", isActivelyTrading)
                        .queryParam("apikey", apiKeyStockScreenerAPI)
                        .build())
                .retrieve()
                .bodyToMono(ScreenedTickersResponse[].class)
                .block();
    }

    public TechnicalIndicatorResponse technicalIndicatorRetrieve(String symbol, LocalDateTime localDateTime) {
        log.info(EXTERNAL_GET_REQUEST_WITH_CRITERIA, "Technical Indicator", "ticker " + symbol + " and start time " + localDateTime);

        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder().codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(1000 * 1024)).build();
        WebClient webClient = WebClient.builder().baseUrl(baseUrlTechnicalIndicatorAPI+"rsi").exchangeStrategies(exchangeStrategies).build();// .build();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("symbol", symbol)
                        .queryParam("interval", Constants.TA_API_PARAMS_INTERVAL)
                        .queryParam("include_ohlc", Constants.TA_API_PARAMS_INCLUDE_OHLC)
                        .queryParam("start_date", localDateTime)
                        .queryParam("apikey", APIkeyTechnicalIndicatorAPI)
                        .build())
                .retrieve()
                .bodyToMono(TechnicalIndicatorResponse.class)
                .block();
    }
//                .onStatus()
//                .doOnError();
//                .doOnNext(x -> System.out.println(x[0]))
//                .subscribe();

//                .bodyToMono(String.class)
//                .doOnNext(x -> System.out.println(x))
//                .subscribe();

}
