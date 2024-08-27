package com.tradingbot.dotty.service.handler;

import com.tradingbot.dotty.models.dto.UserConfigurationDTO;

public interface OrderProcessingService {
    Float getAvailableToTrade(UserConfigurationDTO userConfigurationDTO);
    Float getCurrentPrice();
    void enterPosition(String order);
    void exitPosition(String order);
}
