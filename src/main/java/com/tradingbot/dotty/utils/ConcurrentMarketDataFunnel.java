package com.tradingbot.dotty.utils;

import com.tradingbot.dotty.models.dto.TechnicalIndicatorResponse;
import com.tradingbot.dotty.service.TickerMarketDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j(topic = "Dotty_market_data_funnel")
@Component
public class ConcurrentMarketDataFunnel {

    @Autowired
    TickerMarketDataService tickerMarketDataService;

    @Async
    void processTickerTechnicalAnalysisUpdates(TechnicalIndicatorResponse technicalIndicatorResponse){
        if(technicalIndicatorResponse != null && technicalIndicatorResponse.getValues() != null && technicalIndicatorResponse.getValues().size() >= 1) {
            technicalIndicatorResponse.getValues().stream().forEach( tIRespVals ->
                    {
                        log.info("symbol: {}, RSI: {}, Time: {}, Candle Stick: open: {}, close: {}, high: {}, low: {}",
                                technicalIndicatorResponse.getMeta().getSymbol(), tIRespVals.getRsi(), tIRespVals.getDatetime(), tIRespVals.getOpen(), tIRespVals.getClose(), tIRespVals.getHigh(), tIRespVals.getLow());

//                        tickerMarketDataService.startTrackingForTrade(technicalIndicatorResponse.getMeta(), technicalIndicatorResponse.getMeta().getIndicator(), tIRespVals);
                    }
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