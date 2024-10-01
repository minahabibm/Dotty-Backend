package com.tradingbot.dotty.controllers;

import com.tradingbot.dotty.service.UserConfigurationService;
import com.tradingbot.dotty.utils.ExternalApi.AlpacaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dotty/userTradingAccount")
public class UserTradingAccountController {

    @Autowired
    private UserConfigurationService userConfigurationService;

    @Autowired
    private AlpacaUtil alpacaUtil;


    @GetMapping("/active")
    public ResponseEntity<?> isActiveTradingAccount() {
        return ResponseEntity.ok(userConfigurationService.isUserTradingAccountActive());
    }

    @GetMapping("/account")
    public ResponseEntity<?> getActiveTradingAccounts() {
        return ResponseEntity.ok(alpacaUtil.getAccountDetails(userConfigurationService.getUsersConfigurationsWithActiveTradingAccounts().get(0)));
    }



}

/*
    @Autowired
    @Qualifier("userTradingAccountApiAccessAndPreferencesWebClient")
    WebClient webClient;



    @GetMapping("/accounts")
    public ResponseEntity<?> getTradingAccounts(@RegisteredOAuth2AuthorizedClient("trading-account") OAuth2AuthorizedClient authorizedClient){
        System.out.println(authorizedClient.getAccessToken().getTokenValue());
        WebClient webClient = WebClient.builder().baseUrl("https://api.schwabapi.com/trader/v1/accounts/accountNumbers").build();
        webClient
                .get()
                .uri("https://api.schwabapi.com/trader/v1/accounts/accountNumbers")
                .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> System.out.println("Response from API: " + response)) // Print the response
                .doOnError(error -> System.err.println("Error retrieving resource: " + error.getMessage())); // Handle errors if any
        return ResponseEntity.ok().build();
    }
 */