package com.tradingbot.dotty.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class Backtesting {

    @Autowired
    private Utils utils;

    @Async
    public void backtest(int id) {
        System.out.println("Backtesting " + id);
        switch (id) {
            case 1:
                rsa();
                break;
            default:
                log.error("Invalid ID");
        }
    }

    public void rsa() {
        utils.stockScreenerUpdate();
        utils.tickersTechnicalAnalysis(LocalDateTime.of(2025, 1, 1, 0, 0,0));
    }

}
