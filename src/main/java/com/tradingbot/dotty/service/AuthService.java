package com.tradingbot.dotty.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public interface AuthService {

    String getRedirectUrl(HttpServletRequest httpRequest);
    URI getResponseRedirectUri(HttpServletRequest httpRequest, Authentication authentication) throws URISyntaxException;
    Map<String, Object> getJwtPayloadDecoder(String token);
    void revokeToken(String token);
    boolean validateToken(String token);
    void addOrUpdateAuthenticatedUser(Authentication authentication);
    void getAuthenticUser();
    void extractRequestDetails(HttpServletRequest servletRequest);

}
