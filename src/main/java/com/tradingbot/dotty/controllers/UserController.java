package com.tradingbot.dotty.controllers;

import com.tradingbot.dotty.service.handler.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    ExternalApiService externalApiService;

}