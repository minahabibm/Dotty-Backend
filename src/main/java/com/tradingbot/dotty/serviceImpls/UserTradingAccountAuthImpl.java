package com.tradingbot.dotty.serviceImpls;

import com.tradingbot.dotty.models.dto.AuthUserTradingAccountAccessToken;
import com.tradingbot.dotty.service.UserTradingAccountAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

import static com.tradingbot.dotty.utils.LoggingConstants.EXTERNAL_GET_REQUEST_WITH_CRITERIA;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

@Slf4j
@Service
public class UserTradingAccountAuthImpl implements UserTradingAccountAuth {

    @Value("${trading-account-api.client-id}")
    private String tradingAccountClientId;
    @Value("${trading-account-api.client-secret}")
    private String tradingAccountClientSecret;

    @Override
    public AuthUserTradingAccountAccessToken OAuthFlowAppAuthorization(String code, String session, String redirect_uri) {
        log.info(EXTERNAL_GET_REQUEST_WITH_CRITERIA, "Trading Account");

        AuthUserTradingAccountAccessToken authUserTradingAccountAccessToken = authorizeUserTradingAccountAccessToken(code, redirect_uri);
        System.out.println(authUserTradingAccountAccessToken);

        return authUserTradingAccountAccessToken;
    }

    @Override
    public AuthUserTradingAccountAccessToken authorizeUserTradingAccountAccessToken(String code, String redirect_uri) {
        log.info(EXTERNAL_GET_REQUEST_WITH_CRITERIA, "Trading Account");

        String encodedCredentials = Base64.getEncoder().encodeToString((tradingAccountClientId + ":" + tradingAccountClientSecret).getBytes());
        WebClient webClient = WebClient.builder().baseUrl("https://api.schwabapi.com/v1/oauth/token").build();
        return webClient.post()
                .contentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .header("Authorization", "Basic " + encodedCredentials)
                .body(fromFormData("grant_type", "authorization_code").with("code", code).with("redirect_uri",  redirect_uri))
                .retrieve()
                .bodyToMono(AuthUserTradingAccountAccessToken.class)
                .block();
    }

    @Override
    public AuthUserTradingAccountAccessToken authorizeUserTradingAccountRefreshToken(String RefreshToken) {
        log.info(EXTERNAL_GET_REQUEST_WITH_CRITERIA, "Trading Account");

        String encodedCredentials = Base64.getEncoder().encodeToString((tradingAccountClientId + ":" + tradingAccountClientSecret).getBytes());
        WebClient webClient = WebClient.builder().baseUrl("https://api.schwabapi.com/v1/oauth/token").build();
        return webClient.post()
                .contentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .header("Authorization", "Basic " + encodedCredentials)
                .body(fromFormData("grant_type", "refresh_token").with("refresh_token", RefreshToken))
                .retrieve()
                .bodyToMono(AuthUserTradingAccountAccessToken.class)
                .block();
    }

}
