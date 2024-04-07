package com.tradingbot.dotty.utils;

public class LoggingConstants {
    public static final String ENTITY_CREATE_OPERATION = "Inserting Record {} into Entity {}";
    public static final String ENTITIES_CREATE_OPERATION = "Inserting Bulk Records into Entity {}";
    public static final String ENTITY_READ_OPERATION = "Reading Record {} from Entity {}";
    public static final String ENTITIES_READ_OPERATION = "Reading Bulk Records from Entity {}";
    public static final String ENTITIES_READ_WITH_FILERS_OPERATION = "Reading Records from Entity {} with Criteria {}";
    public static final String ENTITY_UPDATE_OPERATION = "Updating Record {} into Entity {}";
    public static final String ENTITIES_UPDATE_OPERATION = "Updating Bulk Records into Entity {}";
    public static final String ENTITY_DELETE_OPERATION = "Deleting Record {} from Entity {}";
    public static final String ENTITIES_DELETE_OPERATION = "Deleting All Records from Entity {}";

    public static final String SCHEDULED_TASK_START = "Scheduled Task {} start at {}";
    public static final String SCHEDULED_TASK_END = "Halt executing at {}";

    public static final String EXTERNAL_GET_REQUEST_WITH_CRITERIA = "External GET Request - {} with Criteria {}";

    public static final String  MARKET_DATA_FUNNEL = "Concurrent  Thread for {}.";

    public static final String WEBSOCKET_INITIALIZATION = "WebSocket Session initialization";
    public static final String WEBSOCKET_REINITIALIZATION = "WebSocket Session Re-initialization";
    public static final String WEBSOCKET_CONNECTION_STARTED = "WebSocket Connection Established :: {}";
    public static final String WEBSOCKET_CONNECTION_ENDED = "WebSocket Connection Aborted :: {}";
    public static final String WEBSOCKET_TRADES_TICKER_SUBSCRIBE = "Trades Update ::Subscribe Ticker:: {}";
    public static final String WEBSOCKET_TRADES_TICKER_UNSUBSCRIBE = "Trades Update ::Unsubscribe Ticker:: {}";
    public static final String WEBSOCKET_MESSAGE_RECEIVED = "WebSocket Message Received :: type: {}";
    public static final String WEBSOCKET_ABORTING_SESSION = "WebSocket Message Received :: type: {}";

    public static final String SCREENING_TICKERS = "Screening Market for valid Tickers.";
    public static final String SCREENED_TICKERS_SAVING = "Saving screened Tickers {} to Entity Screened Tickers.";
    public static final String SCREENED_TICKERS_PROCESSING = "processing Screened Tickers For market trades.";
    public static final String SCREENED_TICKERS_FILTERING = "Filter Screened Tickers for Sectors";
    public static final String SCREENED_TICKERS_TO_MARKET_TRADE = "Inserting Screened Tickers to market trade updates.";
    public static final String TICKER_TECHNICAL_ANALYSIS = "TICKERS Technical Analysis at {}";
    public static final String TICKER_TECHNICAL_ANALYSIS_SORTED_TICKERS = "Getting Sorted Tickers for Trade Updates.";


}
