package com.tradingbot.dotty.models.dto.requests.Alpaca;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tradingbot.dotty.utils.constants.alpaca.AccountConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountResponse {

    private String id;
    private Map<String, String> admin_configurations;
    private String user_configurations;
    private String account_number;
    private AccountConstants status;
    private String crypto_status;
    private AccountConstants options_approved_level;
    private AccountConstants options_trading_level;
    private String currency;
    private String buying_power;
    private String regt_buying_power;
    private String daytrading_buying_power;
    private String effective_buying_power;
    private String non_marginable_buying_power;
    private String options_buying_power;
    private String bod_dtbp;
    private String cash;
    private String accrued_fees;
    private String portfolio_value;
    private Boolean pattern_day_trader;
    private Boolean trading_blocked;
    private Boolean transfers_blocked;
    private Boolean account_blocked;
    private String created_at;
    private Boolean trade_suspended_by_user;
    private String multiplier;
    private String shorting_enabled;
    private String equity;
    private String last_equity;
    private String long_market_value;
    private String short_market_value;
    private String position_market_value;
    private String initial_margin;
    private String maintenance_margin;
    private String last_maintenance_margin;
    private String sma;
    private Integer daytrade_count;
    private String balance_asof;
    private Integer crypto_tier;
    private String intraday_adjustments;
    private String pending_reg_taf_fees;

}
