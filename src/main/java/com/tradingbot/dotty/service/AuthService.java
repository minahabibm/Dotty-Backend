package com.tradingbot.dotty.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface AuthService {

    String getRedirectUrl(HttpServletRequest httpRequest);
    Map<String, Object> getJwtPayloadDecoder(String token);
    void revokeToken(String token);
    boolean validateToken(String token);
    void addOrUpdateAuthenticatedUser(Authentication authentication);
    void getAuthenticUser();
    void extractRequestDetails(HttpServletRequest servletRequest);

}
