package com.tradingbot.dotty.controllers;

import com.tradingbot.dotty.service.UserConfigurationService;
import com.tradingbot.dotty.service.handler.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dotty/userTradingAccount")
public class UserTradingAccountController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserConfigurationService userConfigurationService;


    @GetMapping("/active")
    public ResponseEntity<?> isActiveTradingAccount() {
        boolean isActive = userConfigurationService.isUserTradingAccountActive(authService.getAuthenticJwtUser().getName());
        Map<String, Boolean> response = new HashMap<>();
        response.put("isActive", isActive);
        return ResponseEntity.ok(response);
    }

}