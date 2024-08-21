package com.tradingbot.dotty.utils;

import com.tradingbot.dotty.models.dto.requests.TechnicalIndicatorResponse;
import com.tradingbot.dotty.models.dto.websockets.TickersUpdateWSMessage;
import com.tradingbot.dotty.service.algoTrading.TickerMarketDataService;
import static com.tradingbot.dotty.utils.constants.LoggingConstants.MARKET_DATA_FUNNEL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;


@Slf4j
@Component
public class ConcurrentMarketDataFunnel {

    @Autowired
    private TickerMarketDataService tickerMarketDataService;


    @Async
    public void processTickerTechnicalAnalysisUpdates(TechnicalIndicatorResponse technicalIndicatorResponse){
//      Thread process will determine position entry\exit, quote&trade, and price updates sub/unsub once in trade.
//        CompletableFuture.runAsync(() -> {
            log.info(MARKET_DATA_FUNNEL, "Technical Analysis Polling");
            List<TechnicalIndicatorResponse.ValuesDetails> technicalIndicatorResponseVal = technicalIndicatorResponse.getValues();
            Collections.reverse(technicalIndicatorResponseVal);  // For stored Intervals Only
            log.warn("response size {}", technicalIndicatorResponseVal.size());
            technicalIndicatorResponseVal.forEach(
                    tIRespVales -> {
                        log.debug("symbol: {}, RSI: {}, Time: {}, Candle Stick: open: {}, close: {}, high: {}, low: {}", technicalIndicatorResponse.getMeta().getSymbol(), tIRespVales.getRsi(), tIRespVales.getDatetime(), tIRespVales.getOpen(), tIRespVales.getClose(), tIRespVales.getHigh(), tIRespVales.getLow());
                        tickerMarketDataService.positionTracker(technicalIndicatorResponse.getMeta().getSymbol(), technicalIndicatorResponse.getMeta().getIndicator(), tIRespVales);
                    });
//        });
    }

    @Async("taskExecutorForHeavyTasks")
    public void processTickerMarketTradeUpdates(List<TickersUpdateWSMessage.TradeDetails> data) throws InterruptedException {
//        return CompletableFuture.runAsync(()-> {
            log.info(MARKET_DATA_FUNNEL,"Market Trades Update");
            tickerMarketDataService.monitorTickerTradesUpdates(data);
            data.forEach(x -> log.debug("{} {} {} {}", x.getS(), x.getP(), x.getV(), Instant.ofEpochMilli(x.getT()).atZone(ZoneId.systemDefault()).toLocalDateTime())); // ZoneId.of("America/New_York")

            //catch (InterruptedException e)
            //catch (RejectedExecutionException e)
//        });
    }

}