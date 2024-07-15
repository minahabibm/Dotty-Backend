package com.tradingbot.dotty.service.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public interface AuthService {

    String getRedirectUrl(HttpServletRequest httpRequest);
    URI getResponseRedirectUri(HttpServletRequest httpRequest, Authentication authentication) throws URISyntaxException;

    Map<String, Object> getJwtPayloadDecoder(String token);
    String getMGMAccessToken();
    void revokeToken(String token);
    boolean validateToken(String token);

    void addOrUpdateAuthenticatedUser(Authentication authentication);
    void getAuthorizationType();
    DefaultOAuth2User getAuthenticOAuth2User();
    JwtAuthenticationToken getAuthenticJwtUser();
    void extractRequestDetails(HttpServletRequest servletRequest);

}
