package com.tradingbot.dotty.controllers;

import com.tradingbot.dotty.service.HoldingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/holdings")
public class HoldingController {

    @Autowired
    private HoldingService holdingService;

    @GetMapping("/compare")
    public ResponseEntity<?> getHoldingsComparison(){
        return ResponseEntity.ok(holdingService.compareHoldings());
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getHoldingsStatistics(){
        return ResponseEntity.ok(holdingService.getHoldingsStatistics());
    }

}
