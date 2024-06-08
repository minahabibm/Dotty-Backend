package com.tradingbot.dotty.service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface AuthService {

    String getRedirectUrl(HttpServletRequest httpRequest);
    Map<String, Object> getJwtPayloadDecoder(String token);
    void revokeToken(String token);
    boolean validateToken(String token);
    void getAuthenticUser();
    void extractRequestDetails(HttpServletRequest servletRequest);

}
