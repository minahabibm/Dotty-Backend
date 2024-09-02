package com.tradingbot.dotty.utils.ExternalAPi;

import com.tradingbot.dotty.models.dto.requests.MarketHoursResponse;
import com.tradingbot.dotty.models.dto.requests.ScreenedTickersResponse;
import com.tradingbot.dotty.models.dto.requests.TechnicalIndicatorResponse;
import com.tradingbot.dotty.utils.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Arrays;

import static com.tradingbot.dotty.utils.constants.Constants.*;
import static com.tradingbot.dotty.utils.constants.Constants.SCREENING_TICKERS_QUERY_PARAMS_IS_ACTIVELY_TRADING;
import static com.tradingbot.dotty.utils.constants.LoggingConstants.EXTERNAL_GET_REQUEST_WITH_CRITERIA;


@Slf4j
@Service
public class TickerUtil {

    @Autowired
    private WebClient webClientTickerStockScreener;

    @Autowired
    private WebClient webClientTickerTechnicalIndicatorRetrieve;

    @Autowired
    private WebClient webClientMarketHours;

    @Value("${stock-screener-api.APIkey}")
    private String apiKeyStockScreenerAPI;

    @Value("${technical-indicators-api.APIkey}")
    private String APIkeyTechnicalIndicatorAPI;

    @Value("${tickers-trades-api.APIkey}")
    private String APIkeyTickersTradesAPI;



    public ScreenedTickersResponse[] stockScreenerUpdateRetrieve() {
        log.info(EXTERNAL_GET_REQUEST_WITH_CRITERIA,  "Stock Screening", "country: " + SCREENING_TICKERS_QUERY_PARAMS_COUNTRY + ", market cap more than: " + SCREENING_TICKERS_QUERY_PARAMS_MARKET_CAP_MORE_THAN + ", exchange: " + Arrays.toString(SCREENING_TICKERS_QUERY_PARAMS_EXCHANGE) + ", beta more than " + SCREENING_TICKERS_QUERY_PARAMS_BETA_MORE_THAN + ", and is actively trading.");
        return webClientTickerStockScreener.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("country", SCREENING_TICKERS_QUERY_PARAMS_COUNTRY)
                        .queryParam("marketCapMoreThan", SCREENING_TICKERS_QUERY_PARAMS_MARKET_CAP_MORE_THAN)
                        .queryParam("exchange", SCREENING_TICKERS_QUERY_PARAMS_EXCHANGE[0])
                        .queryParam("exchange", SCREENING_TICKERS_QUERY_PARAMS_EXCHANGE[1])
                        .queryParam("exchange", SCREENING_TICKERS_QUERY_PARAMS_EXCHANGE[2])
                        .queryParam("betaMoreThan", SCREENING_TICKERS_QUERY_PARAMS_BETA_MORE_THAN)
                        .queryParam("isActivelyTrading", SCREENING_TICKERS_QUERY_PARAMS_IS_ACTIVELY_TRADING)
                        .queryParam("apikey", apiKeyStockScreenerAPI)
                        .build())
                .retrieve()
                .bodyToMono(ScreenedTickersResponse[].class)
                .block();
    }

    public TechnicalIndicatorResponse technicalIndicatorRetrieve(String symbol, LocalDateTime localDateTime) {
        log.info(EXTERNAL_GET_REQUEST_WITH_CRITERIA, "Technical Indicator", "ticker " + symbol + " and start time " + localDateTime);
        return webClientTickerTechnicalIndicatorRetrieve.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rsi")
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

    public MarketHoursResponse marketHoursResponse() {
        log.info(EXTERNAL_GET_REQUEST_WITH_CRITERIA, "Market Hours", "Holidays");
        return webClientMarketHours.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/stock/market-holiday")
                        .queryParam("exchange", "US")
                        .queryParam("token", APIkeyTickersTradesAPI)
                        .build())
                .retrieve()
                .bodyToMono(MarketHoursResponse.class)
                .block();
    }

}
