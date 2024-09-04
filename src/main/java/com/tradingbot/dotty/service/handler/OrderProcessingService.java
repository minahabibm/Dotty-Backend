package com.tradingbot.dotty.service.handler;

import com.tradingbot.dotty.models.dto.UserConfigurationDTO;

public interface OrderProcessingService {

    Double getAvailableToTrade(UserConfigurationDTO userConfigurationDTO);
    Integer getNumberOfShares(String Symbol, Double availableToTrade);


    void enterPosition(String order);
    void exitPosition(String order);
}
