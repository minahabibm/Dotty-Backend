package com.tradingbot.dotty.controllers;

import com.tradingbot.dotty.models.dto.AuthUserTradingAccountAccessToken;
import com.tradingbot.dotty.service.AuthService;
import com.tradingbot.dotty.service.ExternalApiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

import static com.tradingbot.dotty.utils.LoggingConstants.*;

@Slf4j
@RestController
@RequestMapping("/api/dotty/oauth2")
public class AuthenticationController {

    @Autowired
    private AuthService authService;

    @Autowired
    private  ExternalApiService externalApiService;

    @GetMapping("/auth0/login")
    public ResponseEntity<?> doOauth2UserDetailsAndTokensAndRedirectFLowForAuth0(HttpServletRequest httpRequest, HttpServletResponse httpResponse, @RequestParam String code, @RequestParam String state, Authentication authentication) throws URISyntaxException { //AuthRequest
        log.debug(USER_AUTHENTICATION_LOGIN);
        URI redirectUrl = authService.getResponseRedirectUri(httpRequest, authentication);
        if(redirectUrl != null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(redirectUrl);
            log.debug(USER_AUTHENTICATION_LOGIN_SUCCESS);
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).headers(httpHeaders).build();
        } else {
            log.debug(USER_AUTHENTICATION_LOGIN_ERROR);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/userTradingAccount") //"/userAccount"
    public ResponseEntity<?> doOAuthFlowAppAuthorization(@RequestParam String code , @RequestParam String session) throws URISyntaxException {
        AuthUserTradingAccountAccessToken authUserTradingAccountAccessToken = externalApiService.authorizeUserTradingAccountAccessToken(code, session);
        System.out.println(authUserTradingAccountAccessToken);

        URI uri = new URI("https://127.0.0.1");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/refresh")
//    public ResponseEntity<?> doOAuthFlowAppAuthorization(@RegisteredOAuth2AuthorizedClient("auth0") OAuth2AuthorizedClient authorizedClient) {
//
//        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
//        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();
//        if (refreshToken != null) {
//            System.out.println(accessToken.getTokenValue());
//            System.out.println(refreshToken.getTokenValue());
//        } else {
//            System.out.println("No refresh token available");
//        }
//
//        authService.getAuthorizationType();
//
//
//        HashMap<String, String> jsonResponse = new HashMap<>();
//        jsonResponse.put("accessToken", accessToken.getTokenValue());
//        return ResponseEntity.ok(jsonResponse);
//    }

}
