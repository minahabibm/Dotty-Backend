package com.tradingbot.dotty.controllers;

import com.tradingbot.dotty.service.AuthService;
import com.tradingbot.dotty.service.UserAccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/dotty/oauth2/auth0/")
public class AuthorizationController {

    @Autowired
    private AuthService authService;

    @Autowired
    UserAccountService userAccountService;

    private final OAuth2AuthorizedClientService authorizedClientService;

    public AuthorizationController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

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

        String redirectUri = authService.getRedirectUrl(httpRequest);
        System.out.println("user trying to log in " + redirectUri);
//        System.out.println(code + " " + state);
//        System.out.println(authentication.getPrincipal());

        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;
        String clientRegistrationId = authenticationToken.getAuthorizedClientRegistrationId();
        String principalName = authenticationToken.getName();
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(clientRegistrationId, principalName);
        if (authorizedClient != null) {
            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
            OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

            // Do something with the tokens
            System.out.println("Access Token: " + accessToken.getTokenValue());
            if (refreshToken != null) {
                System.out.println("Refresh Token: " + refreshToken.getTokenValue());
            }

            DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
            OidcIdToken idToken = oidcUser.getIdToken();
            System.out.println("Id Token: " + idToken.getTokenValue());

            Map<String, String> response = new HashMap<>();
            response.put("accessToken", accessToken.getTokenValue());
            response.put("idToken", idToken.getTokenValue());


            URI redirectUrl = new URI(redirectUri+"?access_token="+accessToken.getTokenValue()+"&id_token="+idToken.getTokenValue());
            System.out.println(redirectUrl);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(redirectUrl);
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                    .headers(httpHeaders)
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }



}
