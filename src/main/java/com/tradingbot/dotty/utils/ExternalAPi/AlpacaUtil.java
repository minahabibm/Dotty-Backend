package com.tradingbot.dotty.utils.ExternalAPi;

import com.tradingbot.dotty.exceptions.AlpacaExceptions;
import com.tradingbot.dotty.models.dto.UserConfigurationDTO;
import com.tradingbot.dotty.models.dto.requests.Alpaca.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
public class AlpacaUtil {

    @Autowired
    private WebClient webClientAlpaca;

    @Autowired
    private WebClient webClientAlpacaPaper;


    private WebClient.ResponseSpec getAlpacaWebClient(HttpMethod method, String pathVariable, Map<String, String> queryParams, Object body, UserConfigurationDTO userConfigurationDTO) {

        WebClient webClient ;
        if(userConfigurationDTO.getIsActiveTradingAccount())
            webClient =  webClientAlpacaPaper;
        else
            webClient = webClientAlpaca;

        WebClient.RequestBodySpec requestSpec = webClient
                .method(method)
                .uri(uriBuilder -> {
                    uriBuilder.path("/v2/{pathVariable}");
                    if (queryParams != null && !queryParams.isEmpty())
                        queryParams.forEach(uriBuilder::queryParam);
                    return uriBuilder.build(pathVariable);
                })
                .headers(httpHeaders -> {
                    httpHeaders.set("Accept", "application/json");
                    httpHeaders.set("APCA-API-KEY-ID", userConfigurationDTO.getAlpacaApiKey());
                    httpHeaders.set("APCA-API-SECRET-KEY", userConfigurationDTO.getAlpacaSecretKey());
                });

        if ((method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH) && body != null)
            requestSpec.bodyValue(body);

        return requestSpec
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError);

    }

    private Mono<Throwable> handleError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(errorMessage -> {
                    if (response.statusCode().is4xxClientError()) {
                        if (response.statusCode() == HttpStatus.NOT_FOUND) {
                            return Mono.error(new AlpacaExceptions.NotFoundException(errorMessage));
                        } else if (response.statusCode() == HttpStatus.FORBIDDEN) {
                            return Mono.error(new AlpacaExceptions.ForbiddenException(errorMessage));
                        } else if (response.statusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                            return Mono.error(new AlpacaExceptions.UnProcessableEntityException(errorMessage));
                        } else {
                            return Mono.error(new RuntimeException("Client error: " + errorMessage));
                        }
                    } else if (response.statusCode().is5xxServerError()) {
                        if (response.statusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                            return Mono.error(new AlpacaExceptions.InternalServerErrorException(errorMessage));
                        } else {
                            return Mono.error(new RuntimeException("Server error: " + errorMessage));
                        }
                    } else {
                        return Mono.error(new RuntimeException("Unexpected error: " + errorMessage));
                    }
                });
    }


    public AccountResponse getAccountDetails(UserConfigurationDTO userConfigurationDTO) {
        return getAlpacaWebClient(HttpMethod.GET, "account", null, null, userConfigurationDTO)
                .bodyToMono(AccountResponse.class)
                .block();
    }


    public AssetResponse isAssetTradable(String assetSymbol, UserConfigurationDTO userConfigurationDTO) {
        return getAlpacaWebClient(HttpMethod.GET, "assets/" + assetSymbol, null, null, userConfigurationDTO)
                .bodyToMono(AssetResponse.class)
                .block();
    }


    public OrderResponse[] getAllOrders(Map<String, String> orderQueryParams, UserConfigurationDTO userConfigurationDTO) {
        return getAlpacaWebClient(HttpMethod.GET, "orders", orderQueryParams, null, userConfigurationDTO)
                .bodyToMono(OrderResponse[].class)
                .block();
    }

    public OrderResponse createOrder(OrderRequest orderRequestDTO, UserConfigurationDTO userConfigurationDTO) {
        return getAlpacaWebClient(HttpMethod.POST, "orders", null, orderRequestDTO, userConfigurationDTO)
                .bodyToMono(OrderResponse.class)
                .block();
    }

    public OrderResponse.OrderClosed[] cancelAllOpenOrders(UserConfigurationDTO userConfigurationDTO) {
        return getAlpacaWebClient(HttpMethod.DELETE, "orders", null, null, userConfigurationDTO)
                .bodyToMono(OrderResponse.OrderClosed[].class)
                .block();
    }


    public PositionResponse[] getAllPositions(UserConfigurationDTO userConfigurationDTO) {
        return getAlpacaWebClient(HttpMethod.GET, "positions", null, null, userConfigurationDTO)
                .bodyToMono(PositionResponse[].class)
                .block();
    }

    public PositionResponse getAnOpenPosition(String assetSymbol, UserConfigurationDTO userConfigurationDTO) {
        return getAlpacaWebClient(HttpMethod.GET, "positions/" + assetSymbol, null, null, userConfigurationDTO)
                .bodyToMono(PositionResponse.class)
                .block();
    }

    public OrderResponse closePosition(String assetSymbol, Map<String, String> positionQueryParams, UserConfigurationDTO userConfigurationDTO) {
        return getAlpacaWebClient(HttpMethod.DELETE, "positions/" + assetSymbol, positionQueryParams, null, userConfigurationDTO)
                .bodyToMono(OrderResponse.class)
                .block();
    }

    public PositionResponse.PositionClosed[]  closeAllPositions(Map<String, String> positionQueryParams, UserConfigurationDTO userConfigurationDTO) {
        return getAlpacaWebClient(HttpMethod.DELETE, "positions", positionQueryParams, null, userConfigurationDTO)
                .bodyToMono(PositionResponse.PositionClosed[].class)
                .block();
    }

}
