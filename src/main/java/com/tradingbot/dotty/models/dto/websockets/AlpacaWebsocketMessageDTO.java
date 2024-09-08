package com.tradingbot.dotty.models.dto.websockets;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tradingbot.dotty.models.dto.requests.Alpaca.OrderResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlpacaWebsocketMessageDTO {

    private String stream;
    private TradeUpdatesData data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TradeUpdatesData {
        private String status;
        private String action;

        private String[] streams;

        private String event;
        private String execution_id;
        private OrderResponse order;
        private String position_qty;
        private String price;
        private String qty;
        private String timestamp;
    }

}

