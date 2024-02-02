package com.tradingbot.dotty.utils;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTasks {

    @Scheduled(cron = "0 0 8 * * MON,TUE,WED,THU,FRI")
    public void StockScreener() {
        System.out.println("Scheduled cron task - " + System.currentTimeMillis() / 1000);
    }
}
