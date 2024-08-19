package com.tradingbot.dotty.service.handler;

public interface OrderProcessingService {
    Float getAvailableToTrade();
    Float getCurrentPrice();
    void enterPosition(String order);
    void exitPosition(String order);
}
