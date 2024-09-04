package com.tradingbot.dotty.controllers;

import com.tradingbot.dotty.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dotty")
public class DottyController {

    @Autowired
    private Utils utils;

    @PostMapping("/stockScreener")
    public ResponseEntity<?> doStockScreening(){
        utils.stockScreenerUpdate();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/marketHoliday")
    public ResponseEntity<?> getMarketHoliday(){
        return ResponseEntity.ok(utils.getMarketHolidays());
    }

    @GetMapping("/quote/{symbol}")
    public ResponseEntity<?> getTickerQuote(@PathVariable String symbol){
        return ResponseEntity.ok(utils.getTickerCurrentQuote(symbol));
    }


}