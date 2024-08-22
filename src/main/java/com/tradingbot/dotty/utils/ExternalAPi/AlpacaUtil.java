package com.tradingbot.dotty.utils.ExternalAPi;

import com.tradingbot.dotty.models.dto.UserConfigurationDTO;
import com.tradingbot.dotty.models.dto.requests.Alpaca.AccountResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class AlpacaUtil {

    @Autowired
    private WebClient webClientAlpaca;

    @Autowired
    private WebClient webClientAlpacaPaper;


    private WebClient getAlpacaWebClient(Boolean isAlpacaPaperAccount) {
        if(isAlpacaPaperAccount)
            return webClientAlpacaPaper;
        else
            return webClientAlpaca;
    }

    public AccountResponse getAccountDetails(UserConfigurationDTO userConfigurationDTO) {
        return getAlpacaWebClient(userConfigurationDTO.getAlpacaPaperAccount()).get()
                .uri(uriBuilder ->
                        uriBuilder.path("/v2/account").build())
                .headers(httpHeaders -> {
                    httpHeaders.set("APCA-API-KEY-ID", userConfigurationDTO.getAlpacaApiKey());
                    httpHeaders.set("APCA-API-SECRET-KEY", userConfigurationDTO.getAlpacaSecretKey());
                })
                .retrieve()
                .bodyToMono(AccountResponse.class)
                .block();
    }


}
