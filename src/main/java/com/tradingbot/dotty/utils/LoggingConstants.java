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

    public static final String  MARKET_DATA_FUNNEL = "Concurrent Thread for {}.";

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

    public static  final String TICKER_MARKET_DATA_RSI_THRESHOLD = "Verify Ticker Oversold or Overbought.";
    public static  final String TICKER_MARKET_DATA_TICKER_TRACKER = "Verify Ticker {} tracked for Trade.";
    public static  final String TICKER_MARKET_DATA_TRADE_VALIDITY = "Verify Ticker {} is valid for Trade to  open  position.";
    public static  final String TICKER_MARKET_DATA_TRACKING = "Ticker {} position tracker {} for trade.";
    public static  final String TICKER_MARKET_DATA_NOT_TRACKED = "No active position tracker found";
    public static  final String TICKER_MARKET_DATA_TRACKING_POSITION = "Inserting {} Ticker's  position.";

    public static  final String TICKER_MARKET_TRADE_IN_POSITION = "Verify Ticker {} in a Trade." ;
    public static  final String TICKER_MARKET_TRADE_OPEN_POSITION = "Entering position for Ticker {} at price {} on {}." ;
    public static  final String TICKER_MARKET_TRADE_EXIT_POSITION = "Exiting position for Ticker {} at price {} on {}." ;
    public static  final String TICKER_MARKET_TRADE_ADD_TO_HOLDING = "Inserting {} Ticker's position to Holdings" ;
    public static  final String TICKER_MARKET_TRADE_TRADE_VALIDITY = "Verify Ticker {} is valid for Trade TO close Position.";


    public static final String USER_AUTHENTICATION_LOGIN = "Authenticate User trying to login.";
    public static final String USER_AUTHENTICATION_LOGIN_ACCESS_REFRESH_TOKENS = "Obtaining User access and Refresh tokens.";
    public static final String USER_AUTHENTICATION_LOGIN_ID_TOKENS = "Obtaining User Id tokens.";
    public static final String USER_AUTHENTICATION_LOGIN_USER_TO_API = "Adding\\updating user Details to the api.";
    public static final String USER_AUTHENTICATION_LOGIN_SUCCESS = "User Login Successfully completed.";
    public static final String USER_AUTHENTICATION_LOGIN_ERROR = "User Login error ";
    public static final String USER_AUTHENTICATION_LOGOUT = "Authenticate User trying to logout.";
    public static final String USER_AUTHENTICATION_LOGOUT_TOKEN = "Revoking user Access token on logout.";
    public static final String USER_AUTHENTICATION_DATA = "Authentication service handling user data.";
    public static final String USER_AUTHENTICATION_CREATE = "Authentication service adding a new user {}.";
    public static final String USER_AUTHENTICATION_UPDATE = "Authentication service updating user {}.";
    public static final String USER_AUTHENTICATION_GETTING_REDIRECT_URL_TYPE = "Authentication service getting Redirect url type.";
    public static final String USER_AUTHENTICATION_REDIRECT_URL_TYPE = "Redirect url to {}.";
    public static final String USER_AUTHENTICATION_TOKEN_PAYLOAD = "Authentication service Token Decoder.";
    public static final String USER_AUTHENTICATION_TOKEN_DETAILS = "Token Header {} & Payload {}.";
    public static final String USER_AUTHENTICATION_TOKEN_REVOKE = "Authentication service revoke user Access token {}.";
    public static final String USER_AUTHENTICATION_TOKEN_VALIDATION = "Authentication service validating user access token {}.";
    public static final String USER_AUTHENTICATION_TOKEN_GETTING_REVOKED_LIST = "Authentication service Getting Revoked tokens.";
    public static final String USER_AUTHENTICATION_TOKEN_REVOKED_LIST = "Authentication service Revoked tokens {}.";
    public static final String USER_AUTHORIZATION = "Authorize User trying to access api endpoint.";
    public static final String USER_AUTHORIZATION_TOKEN_STATUS = "User uses an access token to access api endpoint: {}.";

    public static final String CACHE_MGM_ACCESS_TOKEN = "Fetching MGM Access Token";
    public static final String CACHE_MGM_ACCESS_TOKEN_EXP = "Expired Access Token, Fetching MGM Access Token from Auth0 API";




}
