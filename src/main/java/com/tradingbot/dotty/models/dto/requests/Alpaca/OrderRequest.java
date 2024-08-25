package com.tradingbot.dotty.models.dto.requests.Alpaca;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tradingbot.dotty.utils.constants.alpaca.TradeConstants;
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
public class OrderRequest {

    private String symbol;
    private String qty;
    private String notional;
    private TradeConstants side;
    private TradeConstants type;
    private TradeConstants time_in_force;
    private String limit_price;
    private String stop_price;
    private String trail_price;
    private String trail_percent;
    private Boolean extended_hours;
    private String client_order_id;
    private TradeConstants order_class;
    private Map<String, String> take_profit;
    private Map<String, String> stop_loss;
    private TradeConstants position_intent;

}
