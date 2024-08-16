package com.tradingbot.dotty.controllers;

import com.tradingbot.dotty.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dotty")
public class DottyController {

    @Autowired
    private Utils utils;

    @GetMapping("/stockScreener")
    public ResponseEntity<?> doStockScreening(){
        utils.stockScreenerUpdate();
        return ResponseEntity.ok().build();
    }

}