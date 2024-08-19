package com.tradingbot.dotty.serviceImpls.handler;

import com.tradingbot.dotty.service.handler.TickerMarketTradeService;
import com.tradingbot.dotty.service.handler.OrderProcessingService;
import com.tradingbot.dotty.utils.TickerUpdatesWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessingServiceImpl implements OrderProcessingService {

    @Autowired
    private TickerMarketTradeService tickerMarketTradeService;

    @Autowired
    private TickerUpdatesWebSocket tickerUpdatesWebSocket;


    @Override
    public Float getAvailableToTrade() {
        return 0f;
    }

    @Override
    public Float getCurrentPrice() {
        return 0f;
    }

    @Override
    public void enterPosition(String order) {
        String[] arr =  order.split(", ");
        String symbol = arr[1];
        String price = arr[2];
        String time = arr[3];
        tickerMarketTradeService.enterPosition(symbol, Float.valueOf(price), time);
        tickerUpdatesWebSocket.subscribeToTickersTradesUpdate(symbol);
    }

    @Override
    public void exitPosition(String order) {
        String[] arr =  order.split(", ");
        String symbol = arr[1];
        String price = arr[2];
        String time = arr[3];
        tickerMarketTradeService.closePosition(symbol, Float.valueOf(price), time);
        tickerUpdatesWebSocket.unsubscribeToTickersTradesUpdate(symbol);
    }
}
