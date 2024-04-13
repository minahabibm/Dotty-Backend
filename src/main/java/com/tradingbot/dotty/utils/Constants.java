package com.tradingbot.dotty.utils;

public class Constants {

    public static final String STOCK_SCREENER_SCHEDULE = "0 56 20 * * MON,TUE,WED,THU,FRI,SAT,SUN";
    public static final String TECHNICAL_ANALYSIS_SCHEDULE = "0 56 20 * * MON,TUE,WED,THU,FRI,SAT,SUN";

    public static final String SCREENING_TICKERS_QUERY_PARAMS_COUNTRY = "US";
    public static final Long SCREENING_TICKERS_QUERY_PARAMS_MARKET_CAP_MORE_THAN = 200000000000L;
    public static final String[] SCREENING_TICKERS_QUERY_PARAMS_EXCHANGE = new String[]{"nasdaq", "nyse", "amex"};
    public static final Double SCREENING_TICKERS_QUERY_PARAMS_BETA_MORE_THAN = 0.0;
    public static final Boolean SCREENING_TICKERS_QUERY_PARAMS_IS_ACTIVELY_TRADING = true;

    public static final Integer SCREENED_TICKERS_NUMBER_OF_SYMBOLS = 10;

    public static final String TA_API_PARAMS_INTERVAL = "5min";
    public static final String TA_API_PARAMS_INCLUDE_OHLC = "true";
    public static final Integer TA_API_MAX_REQUESTS_PER_MIN = 7;
    public static final Integer TA_API_MAX_REQUESTS_REACHED_WAIT_TIME = 60000;
    public static final Integer TA_API_POLLING_RATE = 300;
    public static final Integer TA_API_STOP_POLLING_HOUR= 16;
    public static final Integer TA_API_STOP_POLLING_MINUTE = 0;

    public static final Integer TA_NUMBER_OF_ENTRY_VALIDATION = 2;
    public static final Integer TA_NUMBER_OF_EXIT_VALIDATION = 2;

    public static final Double MAXIMUM_PRICE_ACTION_EXIT = 2.5;


}
