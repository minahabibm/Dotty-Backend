package com.tradingbot.dotty.utils.constants.alpaca;

public enum QueryParamConstants {

    // ======================
    // Order
    // ======================
    STATUS("status", "open", "closed", "all"),
    LIMIT("limit"),
    AFTER("after"),
    UNTIL("until"),
    DIRECTION("direction", "asc", "desc"),
    NESTED("nested", "true", "false"),
    SYMBOLS("symbols"),
    SIDE("side", "buy", "sell"),

    // ======================
    // Positions
    // ======================
    CANCEL_ORDERS("cancel_orders", "true", "false"),
    QTY("qty"),
    PERCENTAGE("percentage");

    private final String key;
    private final String[] values;

    QueryParamConstants(String key, String... values) {
        this.key = key;
        this.values = values;
    }

    public String getKey() {
        return key;
    }

    public String[] getValues() {
        return values;
    }

    public String getDefaultValue() {
        return values.length > 0 ? values[0] : null;
    }

    public String getValue(int index) {
        if (index >= 0 && index < values.length) {
            return values[index];
        }
        throw new IllegalArgumentException("Invalid index for values: " + index);
    }

}
