package com.tradingbot.dotty.utils.constants;

public enum UserTradingAccount {

    SCHWAB("schwab"),
    ALPACA("alpaca");

    public final String value;
    UserTradingAccount(String value) {
        this.value = value;
    }

}
