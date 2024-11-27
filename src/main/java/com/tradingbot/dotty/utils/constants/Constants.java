package com.tradingbot.dotty.utils.constants;

import com.tradingbot.dotty.models.Configuration;

import java.util.List;

public class Constants {

    public static final String STOCK_SCREENER_SCHEDULE = "0 30 8 * * MON,TUE,WED,THU,FRI";
    public static final String TECHNICAL_ANALYSIS_SCHEDULE = "0 */5 8-16 * * MON-FRI";                                  // "0 30 9 * * MON,TUE,WED,THU,FRI";

    public static final String SCREENING_TICKERS_QUERY_PARAMS_COUNTRY = "US";
    public static final Long SCREENING_TICKERS_QUERY_PARAMS_MARKET_CAP_MORE_THAN = 200000000000L;
    public static final String[] SCREENING_TICKERS_QUERY_PARAMS_EXCHANGE = new String[]{"nasdaq", "nyse", "amex"};
    public static final Double SCREENING_TICKERS_QUERY_PARAMS_BETA_MORE_THAN = 0.0;
    public static final Boolean SCREENING_TICKERS_QUERY_PARAMS_IS_ACTIVELY_TRADING = true;

    public static final Integer SCREENED_TICKERS_NUMBER_OF_SYMBOLS = 10;

    public static final String TA_API_PARAMS_INTERVAL = "5min";
    public static final String TA_API_PARAMS_INCLUDE_OHLC = "true";
    public static final Integer TA_API_MAX_REQUESTS_PER_MIN = 7;                                                        // TA_API_POLLING_RATE = 300;
    public static final Integer TA_API_MAX_REQUESTS_REACHED_WAIT_TIME = 60000;
    public static final Integer TA_API_START_POLLING_HOUR= 9;
    public static final Integer TA_API_START_POLLING_MINUTE = 30;
    public static final Integer TA_API_STOP_POLLING_HOUR= 16;
    public static final Integer TA_API_STOP_POLLING_MINUTE = 0;

    public static final Integer TA_NUMBER_OF_ENTRY_VALIDATION = 2;
    public static final Integer TA_NUMBER_OF_EXIT_VALIDATION = 2;

    public static final Double MAXIMUM_PRICE_ACTION_EXIT = 0.5;

    public static final String WEBSOCKET_SESSION_ATTRIBUTE_USER = "user";
    public static final String WEBSOCKET_SUBSCRIPTION_TYPE = "type";
    public static final String WEBSOCKET_SUBSCRIPTION_TOPIC = "topic";
    public static final String WEBSOCKET_SUBSCRIPTION_MESSAGE = "message";
    public static final String WEBSOCKET_SUBSCRIPTION_SESSION_ID = "sessionId";
    public static final String WEBSOCKET_SUBSCRIPTION_USER_ID = "userId";

    public static final List<Configuration> DATA_INITIALIZATION =  List.of(
            new Configuration("TestConfig1", "TestKey1", "TestValue1"),
            new Configuration("TestConfig2", "TestKey2", "TestValue2"),
            new Configuration("TestConfig3", "TestKey3", "TestValue3")
    );

}
