package com.tradingbot.dotty.utils;

import com.tradingbot.dotty.exceptions.Auth0Exceptions;
import com.tradingbot.dotty.models.dto.AccessTokenAudAndJti;
import com.tradingbot.dotty.models.dto.AccessTokenResponse;
import com.tradingbot.dotty.models.dto.ScreenedTickersResponse;
import com.tradingbot.dotty.models.dto.TechnicalIndicatorResponse;

import static com.tradingbot.dotty.utils.Constants.*;
import static com.tradingbot.dotty.utils.LoggingConstants.EXTERNAL_GET_REQUEST_WITH_CRITERIA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Service
public class ExternalApiRequests {

    @Value("${stock-screener-api.base-url}")
    private String baseUrlStockScreenerAPI;
    @Value("${stock-screener-api.APIkey}")
    private String apiKeyStockScreenerAPI;

    @Value("${technical-indicators-api.base-url}")
    private String baseUrlTechnicalIndicatorAPI;
    @Value("${technical-indicators-api.APIkey}")
    private String APIkeyTechnicalIndicatorAPI;

    @Value("${spring.security.oauth2.client.registration.auth0.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.auth0.client-secret}")
    private String clientSecret;

    @Value("${mgm-auth0-api.client-id}")
    private String mgmClientId;
    @Value("${mgm-auth0-api.client-secret}")
    private String mgmClientSecret;
    @Value("${mgm-auth0-api.audience}")
    private String mgmAudience;



    public ScreenedTickersResponse[] stockScreenerUpdateRetrieve() {
        log.info(EXTERNAL_GET_REQUEST_WITH_CRITERIA,  "Stock Screening", "country: " + SCREENING_TICKERS_QUERY_PARAMS_COUNTRY + ", market cap more than: " + SCREENING_TICKERS_QUERY_PARAMS_MARKET_CAP_MORE_THAN + ", exchange: " + Arrays.toString(SCREENING_TICKERS_QUERY_PARAMS_EXCHANGE) + ", beta more than " + SCREENING_TICKERS_QUERY_PARAMS_BETA_MORE_THAN + ", and is actively trading.");

//        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder().codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(500 * 1024)).build();
        WebClient webClient = WebClient.builder().baseUrl(baseUrlStockScreenerAPI).build();               //.exchangeStrategies(exchangeStrategies).build();
        return webClient.get()
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

    @Cacheable(value = "tokens", key = "'mgmAccessToken'")
    public AccessTokenResponse getMGMApiAccessToken() {
        log.debug(EXTERNAL_GET_REQUEST_WITH_CRITERIA, "Auth0 API", "get MGM Access Token");

        WebClient webClient = WebClient.builder().baseUrl("https://dev-z383db7saml34grv.us.auth0.com/oauth/token").build();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", mgmClientId);
        formData.add("client_secret", mgmClientSecret);
        formData.add("audience", mgmAudience);
        return webClient.post()
                .header("content-type", "application/json")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(AccessTokenResponse.class)
                .block();
    }

    @Cacheable(value = "tokens", key = "'revokedTokens'")
    public AccessTokenAudAndJti[] getRevokedAccessTokens(String mgmAccessToken) {
        log.debug(EXTERNAL_GET_REQUEST_WITH_CRITERIA, "Auth0 API", "get Revoked Access Token");

        WebClient webClient = WebClient.builder().baseUrl("https://dev-z383db7saml34grv.us.auth0.com/api/v2/blacklists/tokens").build();
        return webClient.get()
                .header("Authorization", "Bearer " + mgmAccessToken)
                .retrieve()
                .bodyToMono(AccessTokenAudAndJti[].class)
                .block();
    }

    public String revokeAccessToken(String mgmAccessToken, String jti) {
        log.debug(EXTERNAL_GET_REQUEST_WITH_CRITERIA, "Auth0 API", "Revoke user Access token, jti " + jti);

        WebClient webClient = WebClient.builder().baseUrl("https://dev-z383db7saml34grv.us.auth0.com/api/v2/blacklists/tokens").build();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("aud", clientId);
        formData.add("jti", jti);
        return webClient.post()
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + mgmAccessToken)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    if (response.statusCode() == HttpStatus.BAD_REQUEST) {
                        return Mono.error(new Auth0Exceptions.BadRequestException("Invalid request body"));
                    } else if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
                        return Mono.error(new Auth0Exceptions.UnauthorizedAccessException("Invalid token or client is not global"));
                    } else if (response.statusCode() == HttpStatus.FORBIDDEN) {
                        return Mono.error(new Auth0Exceptions.ForbiddenException("Insufficient scope or cannot blacklist tokens for the specified audience"));
                    } else if (response.statusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        return Mono.error(new Auth0Exceptions.TooManyRequestsException("Too many requests"));
                    } else {
                        return Mono.error(new RuntimeException("Unknown client error"));
                    }
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new RuntimeException("Server error")))
                .bodyToMono(String.class)
                .block();
    }


//                .onStatus()
//                .doOnError();
//                .doOnNext(x -> System.out.println(x[0]))
//                .subscribe();

//                .bodyToMono(String.class)
//                .doOnNext(x -> System.out.println(x))
//                .subscribe();

//    public void doOauth2RefreshTokenFlowForAuth0(String refreshToken) {
//        System.out.println("user trying to refresh Token /Web");
//
//        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//        formData.add("grant_type", "refresh_token");
//        formData.add("client_id", clientId);
//        formData.add("client_secret", clientSecret);
//        formData.add("refresh_token", refreshToken);
//
//        WebClient webClient = WebClient.builder().baseUrl(tokenUri).build();
//        webClient.post()
//                .body(BodyInserters.fromFormData(formData))
//                .retrieve()
//                .bodyToMono(String.class)
//                .doOnNext(x -> System.out.println(x))
//                .block();
//
//        return ResponseEntity.ok().build();
//    }
}
