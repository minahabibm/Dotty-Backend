package com.tradingbot.dotty.utils;

import com.tradingbot.dotty.models.dto.TechnicalIndicatorResponse;
import com.tradingbot.dotty.models.dto.TickersUpdateWSMessage;
import com.tradingbot.dotty.service.TickerMarketDataService;
import com.tradingbot.dotty.service.TickerMarketTradeService;
import static com.tradingbot.dotty.utils.LoggingConstants.MARKET_DATA_FUNNEL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;


@Slf4j(topic = "Dotty_market_data_funnel")
@Component
public class ConcurrentMarketDataFunnel {

    @Autowired
    private TickerMarketDataService tickerMarketDataService;

    @Autowired
    private TickerMarketTradeService tickerMarketTradeService;

//    @Async
    void processTickerTechnicalAnalysisUpdates(TechnicalIndicatorResponse technicalIndicatorResponse){
        log.info(MARKET_DATA_FUNNEL,"Technical Analysis Polling");
        if(technicalIndicatorResponse != null && technicalIndicatorResponse.getValues() != null && !technicalIndicatorResponse.getValues().isEmpty()) {
            List<TechnicalIndicatorResponse.ValuesDetails> technicalIndicatorResponseVal = technicalIndicatorResponse.getValues();
            Collections.reverse(technicalIndicatorResponseVal);  // TODO For stored Intervals Only
            technicalIndicatorResponseVal.forEach(
                    tIRespVals -> {
                        log.info("symbol: {}, RSI: {}, Time: {}, Candle Stick: open: {}, close: {}, high: {}, low: {}", technicalIndicatorResponse.getMeta().getSymbol(), tIRespVals.getRsi(), tIRespVals.getDatetime(), tIRespVals.getOpen(), tIRespVals.getClose(), tIRespVals.getHigh(), tIRespVals.getLow());
                        // Thread process will determine position entry\exit -> quote&trade and price updates sub/unsub once in trade.
                        tickerMarketDataService.positionTracker(technicalIndicatorResponse.getMeta().getSymbol(), technicalIndicatorResponse.getMeta().getIndicator(), tIRespVals);
            });
        } else {
                System.out.println(technicalIndicatorResponse);
        }
    }

    @Async("taskExecutorForHeavyTasks")
    void processTickerMarketTradeUpdates(List<TickersUpdateWSMessage.TradeDetails> data) throws InterruptedException {
        log.info(MARKET_DATA_FUNNEL,"Market Trades Update");
        tickerMarketTradeService.monitorTickerTradesUpdates(data);
        data.forEach(x -> log.info("{} {} {} {}", x.getS(), x.getP(), x.getV(), Instant.ofEpochMilli(x.getT()).atZone(ZoneId.systemDefault()).toLocalDateTime())); // ZoneId.of("America/New_York")

        //catch (InterruptedException e)
        //catch (RejectedExecutionException e)
    }

}