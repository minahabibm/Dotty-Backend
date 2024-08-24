package com.tradingbot.dotty.utils.constants.alpaca;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TradeConstants {

    // ======================
    // Asset Classes
    // ======================
    US_EQUITY("us_equity"),
    US_OPTION("us_option"),
    CRYPTO("crypto"),

    // ======================
    // Exchanges
    // ======================
    AMEX("amex"),
    ARCA("arca"),
    BATS("bats"),
    NYSE("nyse"),
    NASDAQ("nasdaq"),
    NYSEARCA("nysearca"),
    OTC("otc"),

    // ======================
    // Statuses
    // ======================
    ACTIVE("active"),
    INACTIVE("inactive"),

    // ======================
    // Order Sides
    // ======================
    BUY("buy"),
    SELL("sell"),

    // ======================
    // Order Types
    // ======================
    MARKET("market"),
    LIMIT("limit"),
    STOP("stop"),
    STOP_LIMIT("stop_limit"),
    TRAILING_STOP("trailing_stop"),

    // ======================
    // Time In Force
    // ======================
    DAY("day"),
    GTC("gtc"),
    OPG("opg"),
    CLS("cls"),
    IOC("ioc"),
    FOK("fok"),

    // ======================
    // Order Classes
    // ======================
    SIMPLE("simple"),
    BRACKET("bracket"),
    OCO("oco"),
    OTO("oto"),

    // ======================
    // Order Statuses
    // ======================
    NEW("new"),
    PARTIALLY_FILLED("partially_filled"),
    FILLED("filled"),
    DONE_FOR_DAY("done_for_day"),
    CANCELED("canceled"),
    EXPIRED("expired"),
    REPLACED("replaced"),
    PENDING_CANCEL("pending_cancel"),
    PENDING_REPLACE("pending_replace"),
    ACCEPTED("accepted"),
    PENDING_NEW("pending_new"),
    ACCEPTED_FOR_BIDDING("accepted_for_bidding"),
    STOPPED("stopped"),
    REJECTED("rejected"),
    SUSPENDED("suspended"),
    CALCULATED("calculated"),

    // ======================
    // Position Intents
    // ======================
    BUY_TO_OPEN("buy_to_open"),
    BUY_TO_CLOSE("buy_to_close"),
    SELL_TO_OPEN("sell_to_open"),
    SELL_TO_CLOSE("sell_to_close");

    private final String value;

    TradeConstants(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Retrieves the corresponding TradeConstants enum from a string value.
     *
     * @param value the string value to convert
     * @return the corresponding TradeConstants enum or null if not found
     */
    @JsonCreator
    public static TradeConstants fromValue(String value) {
        for (TradeConstants constant : TradeConstants.values()) {
            if (constant.value.equalsIgnoreCase(value)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("Unknown constant value: " + value);
    }

}
