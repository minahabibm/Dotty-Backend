package com.tradingbot.dotty.service.handler;

import com.tradingbot.dotty.models.dto.UserConfigurationDTO;

public interface OrderProcessingService {
    Double getAvailableToTrade(UserConfigurationDTO userConfigurationDTO);
    Double getCurrentPrice();
    void enterPosition(String order);
    void exitPosition(String order);
}
