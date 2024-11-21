package com.tradingbot.dotty.serviceImpls.algoTrading;

import com.tradingbot.dotty.models.dto.requests.TechnicalIndicatorResponse;
import com.tradingbot.dotty.models.dto.websockets.TickersUpdateWSMessage;
import com.tradingbot.dotty.service.PositionService;
import com.tradingbot.dotty.service.algoTrading.RSIService;
import com.tradingbot.dotty.service.algoTrading.TickerMarketDataService;
import com.tradingbot.dotty.service.handler.TickerMarketTradeService;
import com.tradingbot.dotty.service.handler.OrderProcessingService;
import com.tradingbot.dotty.utils.constants.Indicators;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;


@Slf4j
@Service
@Transactional
public class TickerMarketDataServiceImpl implements TickerMarketDataService {


    @Autowired
    private RSIService rsiService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private TickerMarketTradeService tickerMarketTradeService;

    @Autowired
    private OrderProcessingService orderProcessingService;



    @Override
    public boolean isReadyToOpenPosition(String symbol) {
        return rsiService.isReadyToOpenTrade(symbol);
    }

    @Override
    public boolean isReadyToClosePosition(String symbol) {
        return  rsiService.isReadyToExitTrade(symbol);
    }



    @Override
    public void monitorTickerTradesUpdates() {

    }

    @Override
    public void monitorTickerTradesUpdates(List<TickersUpdateWSMessage.TradeDetails> data) {

    }


    @Override
    public boolean isPriceIncreasing() {
        return false;
    }

    @Override
    public boolean isPriceDecreasing() {
        return false;
    }

    @Override
    public void processTrackerStatus(String symbol, String tIndicatorName, TechnicalIndicatorResponse.ValuesDetails tiValuesDetails) {
        if (tickerMarketTradeService.isTickerTracked(symbol)) {
            tickerMarketTradeService.updateTrackingForTrade(symbol, tIndicatorName, tiValuesDetails);

            if(!tickerMarketTradeService.isInTrade(symbol)){
                if(rsiService.isReadyToStopTracking(tiValuesDetails))
                    tickerMarketTradeService.stopTrackingForTrade(symbol);
            }
        } else {
            if (rsiService.isReadyToTrack(tiValuesDetails))
                tickerMarketTradeService.startTrackingForTrade(symbol, tIndicatorName, tiValuesDetails, Indicators.RSI);
        }
    }

    @Override
    public void positionTracker(String symbol, TechnicalIndicatorResponse.IndicatorDetails tIndicatorDetails, TechnicalIndicatorResponse.ValuesDetails tiValuesDetails) {
        log.debug(TICKER_MARKET_TRADE_POSITION, symbol);

        if(positionService.getPositionBySymbolAndIntervals(symbol, LocalDateTime.parse(tiValuesDetails.getDatetime().replaceFirst(" ","T"))).isEmpty()) {
            processTrackerStatus(symbol, tIndicatorDetails.getName(), tiValuesDetails);
            if (tickerMarketTradeService.isInTrade(symbol)) {
                if(isReadyToClosePosition(symbol)){
                    orderProcessingService.exitPosition("Exit order for stock, " + symbol + ", " + tiValuesDetails.getClose() +  ", " + tiValuesDetails.getDatetime());
//                  orderProducer.sendEntryOrder("Exit order for stock, " + symbol + ", " + tiValuesDetails.getClose() +  ", " + tiValuesDetails.getDatetime());
                }
            } else if(isReadyToOpenPosition(symbol)) {
                orderProcessingService.enterPosition("Entry order for stock , " + symbol + ", " + tiValuesDetails.getClose()+  ", " + tiValuesDetails.getDatetime());
//              orderProducer.sendEntryOrder("Entry order for stock , " + symbol + ", " + tiValuesDetails.getClose()+  ", " + tiValuesDetails.getDatetime());
            }

        }
    }

}
