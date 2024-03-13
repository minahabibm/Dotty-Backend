package com.tradingbot.dotty.utils;

import com.tradingbot.dotty.models.dto.TechnicalIndicatorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j(topic = "Dotty_market_data_funnel")
@Component
public class ConcurrentMarketDataFunnel {

    @Async
    void processTickerTechnicalAnalysisUpdates(TechnicalIndicatorResponse technicalIndicatorResponse){
        if(technicalIndicatorResponse != null && technicalIndicatorResponse.getValues() != null && technicalIndicatorResponse.getValues().size() >= 1) {
            log.info("symbol: {}, RSI: {}, Time: {}, Candle Stick: open: {}, close: {}, high: {}, low: {}",
                    technicalIndicatorResponse.getMeta().getSymbol(),
                    technicalIndicatorResponse.getValues().get(0).getRsi(),
                    technicalIndicatorResponse.getValues().get(0).getDatetime(),
                    technicalIndicatorResponse.getValues().get(0).getOpen(),
                    technicalIndicatorResponse.getValues().get(0).getClose(),
                    technicalIndicatorResponse.getValues().get(0).getHigh(),
                    technicalIndicatorResponse.getValues().get(0).getLow()
            );
        } else{
                System.out.println(technicalIndicatorResponse);
        }
        // Thread process will determine position entry\exit -> quote&trade and price updates sub/unsub once in trade.

    }

    @Async("taskExecutorForHeavyTasks")
    void processTickerMarketTradeUpdates() throws InterruptedException {
        System.out.println(Thread.currentThread().getName());
        //catch (InterruptedException e)
        //catch (RejectedExecutionException e)
    }

}