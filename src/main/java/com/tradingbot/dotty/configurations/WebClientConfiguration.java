package com.tradingbot.dotty.configurations;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Value("${stock-screener-api.base-url}")
    private String baseUrlStockScreenerAPI;

    @Value("${technical-indicators-api.base-url}")
    private String baseUrlTechnicalIndicatorAPI;

    @Value("${tickers-trades-api.base-url}")
    private String baseUrlTickersTradesAPI;

    @Value("${alpaca-api.base-url}")
    private String baseUrlAlpacaAPI;

    @Value("${alpaca-api.paper-base-url}")
    private String baseUrlAlpacaPaperAPI;


    @Bean
    public WebClient webClientTickerStockScreener(WebClient.Builder builder) {
        return builder.baseUrl(baseUrlStockScreenerAPI).build();
    }

    @Bean
    public WebClient webClientTickerTechnicalIndicatorRetrieve(WebClient.Builder builder) {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder().codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(1000 * 1024)).build();
        return builder
                .baseUrl(baseUrlTechnicalIndicatorAPI)
                .exchangeStrategies(exchangeStrategies)
                .build();
    }

    @Bean
    public WebClient webClientMarketHours(WebClient.Builder builder) {
        return builder.baseUrl(baseUrlTickersTradesAPI).build();
    }

    @Bean
    public WebClient webClientAuth0MGMApiAccessToken(WebClient.Builder builder) {
        return builder.baseUrl("https://dev-z383db7saml34grv.us.auth0.com/oauth/token").build();
    }

    @Bean
    public WebClient webClientAuth0RevokedAccessTokens(WebClient.Builder builder) {
        return builder.baseUrl("https://dev-z383db7saml34grv.us.auth0.com/api/v2/blacklists/tokens").build();
    }

    @Bean
    public WebClient webClientSchwabAccessToken(WebClient.Builder builder) {
        return builder.baseUrl("https://api.schwabapi.com/v1/oauth/token").build();
    }

    @Bean
    public WebClient webClientAlpaca(WebClient.Builder builder) {
        return builder.baseUrl(baseUrlAlpacaAPI).build();
    }

    @Bean
    public WebClient webClientAlpacaPaper(WebClient.Builder builder) {
        return builder.baseUrl(baseUrlAlpacaPaperAPI).build();
    }


}
