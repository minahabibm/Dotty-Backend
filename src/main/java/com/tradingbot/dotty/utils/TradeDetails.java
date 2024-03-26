package com.tradingbot.dotty.utils;

public enum TradeDetails {
    OVERSOLD(30, "Buy", "Long"),
    OVERBOUGHT(70, "Sell",  "Short" );


    public final Integer value;
    public final String order;
    public final String orderType ;

    TradeDetails(Integer value, String order, String orderType) {
        this.value = value;
        this.order = order;
        this.orderType = orderType;
    }
}
