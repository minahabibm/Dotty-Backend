package com.tradingbot.dotty.service.handler;

import com.tradingbot.dotty.models.dto.HoldingDTO;
import com.tradingbot.dotty.models.dto.OrdersDTO;
import com.tradingbot.dotty.models.dto.requests.TechnicalIndicatorResponse;

public interface TickerMarketTradeService {

    void startOrUpdateTrackingForTrade(String symbol , TechnicalIndicatorResponse.IndicatorDetails tIndicatorDetails, TechnicalIndicatorResponse.ValuesDetails tiValuesDetails);
    void stopTrackingForTrade(String symbol);
    boolean isTickerTracked(String symbol);
    boolean isInTrade(String symbol);
    String getOrderType(boolean toOpen, String typeOfTrade);
    OrdersDTO enterPosition(String symbol, Float entryPrice, String entryTime);
    OrdersDTO closePosition(String symbol, Float exitPrice, String exitTime);
    HoldingDTO addToHolding(Long positionTrackerId);

}
