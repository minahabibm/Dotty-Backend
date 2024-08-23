package com.tradingbot.dotty.utils.ExternalAPi;

import com.tradingbot.dotty.models.dto.UserConfigurationDTO;
import com.tradingbot.dotty.models.dto.requests.Alpaca.AccountResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class AlpacaUtil {

    @Autowired
    private WebClient webClientAlpaca;

    @Autowired
    private WebClient webClientAlpacaPaper;


    private WebClient.RequestBodySpec getAlpacaWebClient(HttpMethod method, String pathVariable, Map<String, String> queryParams, String body, UserConfigurationDTO userConfigurationDTO) {

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

        return requestSpec;
    }


    public AccountResponse getAccountDetails(UserConfigurationDTO userConfigurationDTO) {
        return getAlpacaWebClient(HttpMethod.GET, "account", null, null, userConfigurationDTO)
                .retrieve()
                .bodyToMono(AccountResponse.class)
                .block();
    }


//    public AccountResponse isAssetTradable(UserConfigurationDTO userConfigurationDTO) {
//        return getAlpacaWebClient(userConfigurationDTO.getAlpacaPaperAccount()).get()
//
//                .retrieve()
//                .bodyToMono(AccountResponse.class)
//                .block();
//    }
//
//    public AccountResponse createOrder(UserConfigurationDTO userConfigurationDTO) {
//        return getAlpacaWebClient(userConfigurationDTO.getAlpacaPaperAccount()).get()
//
//                .retrieve()
//                .bodyToMono(AccountResponse.class)
//                .block();
//    }
//
//    public AccountResponse getAllOrders(UserConfigurationDTO userConfigurationDTO) {
//        return getAlpacaWebClient(userConfigurationDTO.getAlpacaPaperAccount()).get()
//
//                .retrieve()
//                .bodyToMono(AccountResponse.class)
//                .block();
//    }
//
//    public AccountResponse cancelAllOpenOrders(UserConfigurationDTO userConfigurationDTO) {
//        return getAlpacaWebClient(userConfigurationDTO.getAlpacaPaperAccount()).get()
//
//                .retrieve()
//                .bodyToMono(AccountResponse.class)
//                .block();
//    }
//
//    public AccountResponse getAllPositions(UserConfigurationDTO userConfigurationDTO) {
//        return getAlpacaWebClient(userConfigurationDTO.getAlpacaPaperAccount()).get()
//
//                .retrieve()
//                .bodyToMono(AccountResponse.class)
//                .block();
//    }
//
//    public AccountResponse closeOpenPositions(UserConfigurationDTO userConfigurationDTO) {
//        return getAlpacaWebClient(userConfigurationDTO.getAlpacaPaperAccount()).get()
//
//                .retrieve()
//                .bodyToMono(AccountResponse.class)
//                .block();
//    }
//
//    public AccountResponse getAnOpenPosition(UserConfigurationDTO userConfigurationDTO) {
//        return getAlpacaWebClient(userConfigurationDTO.getAlpacaPaperAccount()).get()
//
//                .retrieve()
//                .bodyToMono(AccountResponse.class)
//                .block();
//    }
//
//    public AccountResponse closePosition(UserConfigurationDTO userConfigurationDTO) {
//        return getAlpacaWebClient(userConfigurationDTO.getAlpacaPaperAccount()).get()
//
//                .retrieve()
//                .bodyToMono(AccountResponse.class)
//                .block();
//    }

}
