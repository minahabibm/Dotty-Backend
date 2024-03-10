package com.tradingbot.dotty.utils;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ConcurrentMarketDataFunnel {

    @Async
    void processTickerTechnicalAnalysisUpdates(){
        System.out.println(Thread.currentThread().getName());
    }
    @Async("taskExecutorForHeavyTasks")
    void processTickerMarketTradeUpdates() throws InterruptedException {
        System.out.println(Thread.currentThread().getName());
        //catch (InterruptedException e)
        //catch (RejectedExecutionException e)
    }

}