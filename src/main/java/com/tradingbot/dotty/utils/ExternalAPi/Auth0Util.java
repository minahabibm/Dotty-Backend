package com.tradingbot.dotty.utils.ExternalAPi;

import com.tradingbot.dotty.exceptions.Auth0Exceptions;
import com.tradingbot.dotty.models.dto.requests.AccessTokenAudAndJti;
import com.tradingbot.dotty.models.dto.requests.AccessTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.EXTERNAL_GET_REQUEST_WITH_CRITERIA;


@Slf4j
@Service
public class Auth0Util {

    @Autowired
    private WebClient webClientAuth0MGMApiAccessToken;

    @Autowired
    private WebClient webClientAuth0RevokedAccessTokens;

    @Value("${spring.security.oauth2.client.registration.auth0-mgm.client-id}")
    private String mgmClientId;
    @Value("${spring.security.oauth2.client.registration.auth0-mgm.client-secret}")
    private String mgmClientSecret;
    @Value("${oauth2-info.auth0-mgm.audience}")
    private String mgmAudience;

    @Value("${spring.security.oauth2.client.registration.auth0.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.auth0.client-secret}")
    private String clientSecret;



    @Cacheable(value = "tokens", key = "'mgmAccessToken'")
    public AccessTokenResponse getMGMApiAccessToken() {
        log.debug(EXTERNAL_GET_REQUEST_WITH_CRITERIA, "Auth0 API", "get MGM Access Token");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", mgmClientId);
        formData.add("client_secret", mgmClientSecret);
        formData.add("audience", mgmAudience);
        return webClientAuth0MGMApiAccessToken.post()
                .header("content-type", "application/json")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(AccessTokenResponse.class)
                .block();

    }

    @Cacheable(value = "tokens", key = "'revokedTokens'")
    public AccessTokenAudAndJti[] getRevokedAccessTokens(String mgmAccessToken) {
        log.debug(EXTERNAL_GET_REQUEST_WITH_CRITERIA, "Auth0 API", "get Revoked Access Token");

        return webClientAuth0RevokedAccessTokens.get()
                .header("Authorization", "Bearer " + mgmAccessToken)
                .retrieve()
                .bodyToMono(AccessTokenAudAndJti[].class)
                .block();
    }

    public String revokeAccessToken(String mgmAccessToken, String jti) {
        log.debug(EXTERNAL_GET_REQUEST_WITH_CRITERIA, "Auth0 API", "Revoke user Access token, jti " + jti);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("aud", clientId);
        formData.add("jti", jti);
        return webClientAuth0RevokedAccessTokens.post()
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




}
