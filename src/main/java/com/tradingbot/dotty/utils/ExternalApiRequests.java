package com.tradingbot.dotty.utils;

import com.tradingbot.dotty.models.ScreenedTicker;
import com.tradingbot.dotty.models.TickersTradeUpdates;
import com.tradingbot.dotty.models.dto.ScreenedTickerDTO;
import com.tradingbot.dotty.models.dto.ScreenedTickersResponse;
import com.tradingbot.dotty.models.dto.TechnicalIndicatorResponse;
import com.tradingbot.dotty.service.ScreenedTickersService;
import com.tradingbot.dotty.service.TickersTradeUpdatesService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j(topic = "Dotty_Ticker_External_API_Requests")
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
        log.info("Stock Screening ::GET Request:: with Criteria country {}, market cap more than {}, exchange {}," +
                        " beta more than {}, and is actively trading."
                , country, marketCapMoreThan, exchange, betaMoreThan);

//        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder().codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(500 * 1024)).build();
        WebClient webClient = WebClient.builder().baseUrl(baseUrlStockScreenerAPI).build();               //.exchangeStrategies(exchangeStrategies).build();
        ScreenedTickersResponse[] screenedTickersResponse = webClient.get()
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
//                .onStatus()
//                .doOnError();
//                .doOnNext(x -> System.out.println(x[0]))
//                .subscribe();

        return screenedTickersResponse;
    }

    public TechnicalIndicatorResponse technicalIndicatorRetrieve(String symbol, LocalDateTime localDateTime) {
        log.info("Technical Indicator ::GET Request:: for ticker {}, start time {}", symbol, localDateTime);

        WebClient webClient = WebClient.builder().baseUrl(baseUrlTechnicalIndicatorAPI+"rsi").build();
        TechnicalIndicatorResponse technicalIndicatorResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("symbol", symbol)
                        .queryParam("interval", "5min")
                        .queryParam("include_ohlc", "true")
                        .queryParam("start_date", localDateTime)
                        .queryParam("apikey", APIkeyTechnicalIndicatorAPI)
                        .build())
                .retrieve()
                .bodyToMono(TechnicalIndicatorResponse.class)
                .block();
//                .bodyToMono(String.class)
//                .doOnNext(x -> System.out.println(x))
//                .subscribe();

        return technicalIndicatorResponse;
    }

}
