package com.tradingbot.dotty.utils.ExternalApi;

import com.tradingbot.dotty.models.dto.requests.AuthUserTradingAccountAccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.EXTERNAL_GET_REQUEST_WITH_CRITERIA;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;


@Slf4j
@Service
public class SchwabUtil {

    @Autowired
    private WebClient webClientSchwabAccessToken;

    @Value("${spring.security.oauth2.client.registration.trading-account.client-id}")
    private String tradingAccountClientId;
    @Value("${spring.security.oauth2.client.registration.trading-account.client-secret}")
    private String tradingAccountClientSecret;
    @Value("${spring.security.oauth2.client.registration.trading-account.redirect-uri}")
    private String tradingAccountRedirectUri;


    public AuthUserTradingAccountAccessToken authorizeUserTradingAccountAccessToken(String code, String session) {
        log.info(EXTERNAL_GET_REQUEST_WITH_CRITERIA, "Trading Account", "authorize User Trading Account AccessToken");

        String encodedCredentials = Base64.getEncoder().encodeToString((tradingAccountClientId + ":" + tradingAccountClientSecret).getBytes());
        return webClientSchwabAccessToken.post()
                .contentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .header("Authorization", "Basic " + encodedCredentials)
                .body(fromFormData("grant_type", "authorization_code").with("code", code).with("redirect_uri",  tradingAccountRedirectUri))
                .retrieve()
                .bodyToMono(AuthUserTradingAccountAccessToken.class)
                .block();
    }

    public AuthUserTradingAccountAccessToken authorizeUserTradingAccountRefreshToken(String RefreshToken) {
        log.info(EXTERNAL_GET_REQUEST_WITH_CRITERIA, "Trading Account", "authorize User Trading Account RefreshToken");

        String encodedCredentials = Base64.getEncoder().encodeToString((tradingAccountClientId + ":" + tradingAccountClientSecret).getBytes());
        return webClientSchwabAccessToken.post()
                .contentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .header("Authorization", "Basic " + encodedCredentials)
                .body(fromFormData("grant_type", "refresh_token").with("refresh_token", RefreshToken))
                .retrieve()
                .bodyToMono(AuthUserTradingAccountAccessToken.class)
                .block();
    }

}
