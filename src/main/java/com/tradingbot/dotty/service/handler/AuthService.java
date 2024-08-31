package com.tradingbot.dotty.service.handler;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public interface AuthService {

    <T> T getAuthenticatedUser();

    String getRedirectUrl(HttpServletRequest httpRequest);
    URI getResponseRedirectUri(HttpServletRequest httpRequest) throws URISyntaxException;

    void addOrUpdateAuthenticatedUser();

    Map<String, Object> getJwtPayloadDecoder(String token);
    String getMGMAccessToken();
    void revokeToken(String token);
    boolean validateToken(String token);

    void extractRequestDetails(HttpServletRequest servletRequest);

}
