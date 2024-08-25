package com.tradingbot.dotty.models.dto.requests.Alpaca;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tradingbot.dotty.utils.constants.alpaca.TradeConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse {

    private String id;
    private String client_order_id;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime submitted_at;
    private LocalDateTime filled_at;
    private LocalDateTime expired_at;
    private LocalDateTime canceled_at;
    private LocalDateTime failed_at;
    private LocalDateTime replaced_at;
    private String replaced_by;
    private String replaces;
    private String asset_id;
    private String symbol;
    private TradeConstants asset_class;
    private String notional;
    private String qty;
    private String filled_qty;
    private String filled_avg_price;
    private TradeConstants order_class;
    private TradeConstants order_type;
    private TradeConstants type;
    private TradeConstants side;
    private TradeConstants time_in_force;
    private String limit_price;
    private String stop_price;
    private TradeConstants status;
    private boolean extended_hours;
    private List<OrderResponse> legs;
    private String trail_percent;
    private String trail_price;
    private String hwm;
    private TradeConstants position_intent;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderClosed {
        private String id;
        private String status;
    }

}
