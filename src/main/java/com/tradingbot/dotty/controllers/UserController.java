package com.tradingbot.dotty.controllers;

import com.tradingbot.dotty.service.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    ExternalApiService externalApiService;

    @GetMapping("/") //"/userAccount"
    public ResponseEntity<?> doOAuthFlowAppAuthorization(@RequestParam String code , @RequestParam String session) throws URISyntaxException {
        externalApiService.OAuthFlowAppAuthorization(code, session, "https://127.0.0.1");

        URI uri = new URI("https://127.0.0.1");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);
        return ResponseEntity.ok().build();
    }

}
