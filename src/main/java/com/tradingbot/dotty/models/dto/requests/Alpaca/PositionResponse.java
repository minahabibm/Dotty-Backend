package com.tradingbot.dotty.models.dto.requests.Alpaca;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tradingbot.dotty.utils.constants.alpaca.TradeConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PositionResponse {

    private String assetId;
    private String symbol;
    private TradeConstants exchange;
    private TradeConstants assetClass;
    private String avgEntryPrice;
    private String qty;
    private String qtyAvailable;
    private String side;
    private String marketValue;
    private String costBasis;
    private String unrealizedPl;
    private String unrealizedPlpc;
    private String unrealizedIntradayPl;
    private String unrealizedIntradayPlpc;
    private String currentPrice;
    private String lastdayPrice;
    private String changeToday;
    private Boolean assetMarginable;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PositionClosed {
        private String symbol;
        private String status;
        private OrderResponse body;
    }
}

