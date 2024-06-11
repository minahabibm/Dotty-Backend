package com.tradingbot.dotty.controllers;

import com.tradingbot.dotty.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

import static com.tradingbot.dotty.utils.LoggingConstants.*;

@Slf4j
@RestController
@RequestMapping("/api/dotty/oauth2/auth0/")
public class AuthenticationController {

    @Autowired
    private AuthService authService;

    @GetMapping("/") //"/userAccount"
    public ResponseEntity<?> doOAuthFlowAppAuthorization(@RegisteredOAuth2AuthorizedClient("auth0") OAuth2AuthorizedClient authorizedClient) {

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();
        if (refreshToken != null) {
            System.out.println(accessToken.getTokenValue());
            System.out.println(refreshToken.getTokenValue());
        } else {
            System.out.println("No refresh token available");
        }



        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            System.out.println("JwtAuthenticationToken");
            // Handle JWT-based authentication
            String token = jwtAuth.getToken().getTokenValue();
            // Process the JWT token
        } else if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Auth = (OAuth2AuthenticationToken) authentication;
            System.out.println("OAuth2AuthenticationToken");
            // Handle OAuth2 client-based authentication
            String principalName = oauth2Auth.getPrincipal().getName();
            // Process the OAuth2 authentication
        } else {
            throw new IllegalArgumentException("Unexpected authentication type: " + authentication.getClass());
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/login")
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

}
