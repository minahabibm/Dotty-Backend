package com.tradingbot.dotty.utils.constants.alpaca;

public enum AccountConstants {

    // ======================
    // Account Status
    // ======================
    ONBOARDING("ONBOARDING"),
    SUBMISSION_FAILED("SUBMISSION_FAILED"),
    SUBMITTED("SUBMITTED"),
    ACCOUNT_UPDATED("ACCOUNT_UPDATED"),
    APPROVAL_PENDING("APPROVAL_PENDING"),
    ACTIVE("ACTIVE"),
    REJECTED("REJECTED"),

    // ======================
    // Options Trading Level
    // ======================
    OPTIONS_DISABLED(0),
    COVERED_CALL_CASH_SECURED_PUT(1),
    LONG_CALL_PUT(2);

    private final String stringValue;
    private final Integer intValue;

    AccountConstants(String value) {
        this.stringValue = value;
        this.intValue = null;
    }

    AccountConstants(Integer value) {
        this.stringValue = null;
        this.intValue = value;
    }

    public String getStringValue() {
        return stringValue;
    }

    public Integer getIntValue() {
        return intValue;
    }

    /**
     * Retrieves the corresponding AccountResponseConstants enum from a string value.
     *
     * @param value the string value to convert
     * @return the corresponding AccountResponseConstants enum or null if not found
     */
    public static AccountConstants fromStringValue(String value) {
        for (AccountConstants constant : AccountConstants.values()) {
            if (constant.stringValue != null && constant.stringValue.equalsIgnoreCase(value)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("Unknown string constant value: " + value);
    }

    /**
     * Retrieves the corresponding AccountResponseConstants enum from an integer value.
     *
     * @param value the integer value to convert
     * @return the corresponding AccountResponseConstants enum or null if not found
     */
    public static AccountConstants fromIntValue(Integer value) {
        for (AccountConstants constant : AccountConstants.values()) {
            if (constant.intValue != null && constant.intValue.equals(value)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("Unknown integer constant value: " + value);
    }

}
