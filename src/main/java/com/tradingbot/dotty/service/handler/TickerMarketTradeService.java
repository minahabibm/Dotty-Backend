package com.tradingbot.dotty.service.handler;

import com.tradingbot.dotty.models.dto.requests.TechnicalIndicatorResponse;

public interface TickerMarketTradeService {

    void startOrUpdateTrackingForTrade(String symbol , TechnicalIndicatorResponse.IndicatorDetails tIndicatorDetails, TechnicalIndicatorResponse.ValuesDetails tiValuesDetails);
    void stopTrackingForTrade(String symbol);
    boolean isTickerTracked(String symbol);
    boolean isInTrade(String symbol);
    void enterPosition(String symbol, Float entryPrice, String entryTime);
    void closePosition(String symbol, Float exitPrice, String exitTime);
    void addToHolding(Long positionTrackerId);

}
