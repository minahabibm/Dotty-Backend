package com.tradingbot.dotty.service.handler;

import com.tradingbot.dotty.models.dto.HoldingDTO;
import com.tradingbot.dotty.models.dto.OrdersDTO;
import com.tradingbot.dotty.models.dto.requests.TechnicalIndicatorResponse;
import com.tradingbot.dotty.utils.constants.Indicators;

import java.util.Optional;

public interface TickerMarketTradeService {

    void startTrackingForTrade(String symbol , String tIndicatorName, TechnicalIndicatorResponse.ValuesDetails tiValuesDetails, Indicators indicator);
    void updateTrackingForTrade(String symbol , String tIndicatorName, TechnicalIndicatorResponse.ValuesDetails tiValuesDetails);
    void stopTrackingForTrade(String symbol);
    boolean isTickerTracked(String symbol);
    boolean isInTrade(String symbol);
    String getOrderType(boolean toOpen, String typeOfTrade);
    Optional<OrdersDTO> enterPosition(String symbol, Float entryPrice, String entryTime);
    Optional<OrdersDTO> closePosition(String symbol, Float exitPrice, String exitTime);
    Optional<HoldingDTO> addToHolding(Long positionTrackerId);

}
