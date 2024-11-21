package com.tradingbot.dotty.service.algoTrading;

import com.tradingbot.dotty.models.dto.requests.TechnicalIndicatorResponse;
import com.tradingbot.dotty.models.dto.websockets.TickersUpdateWSMessage;

import java.util.List;

public interface TickerMarketDataService {

    boolean isReadyToOpenPosition(String symbol);
    boolean isReadyToClosePosition(String symbol);

    void monitorTickerTradesUpdates();
    void monitorTickerTradesUpdates(List<TickersUpdateWSMessage.TradeDetails> data);

    boolean isPriceIncreasing();
    boolean isPriceDecreasing();

    void processTrackerStatus(String symbol, String tIndicatorName, TechnicalIndicatorResponse.ValuesDetails tiValuesDetails);
    void positionTracker(String symbol, TechnicalIndicatorResponse.IndicatorDetails tIndicatorDetails, TechnicalIndicatorResponse.ValuesDetails tiValuesDetails);

}
