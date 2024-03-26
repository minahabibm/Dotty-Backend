package com.tradingbot.dotty.utils;

public class Constants {

    public static final String STOCK_SCREENER_SCHEDULE = "0 56 20 * * MON,TUE,WED,THU,FRI,SAT,SUN";
    public static final String TECHNICAL_ANALYSIS_SCHEDULE = "0 56 20 * * MON,TUE,WED,THU,FRI,SAT,SUN";

    public static final Integer SCREENED_TICKERS_NUMBER_OF_SYMBOLS = 10;

    public static final String TA_API_PARAMS_INTERVAL = "5min";
    public static final String TA_API_PARAMS_INCLUDE_OHLC = "true";
    public static final Integer TA_API_MAX_REQUESTS_PER_MIN = 7;
    public static final Integer TA_API_MAX_REQUESTS_REACHED_WAIT_TIME = 60000;
    public static final Integer TA_API_POLLING_RATE = 300;
    public static final Integer TA_API_STOP_POLLING_HOUR= 16;
    public static final Integer TA_API_STOP_POLLING_MINUTE = 0;

}
