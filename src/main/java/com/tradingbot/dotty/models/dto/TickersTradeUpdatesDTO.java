package com.tradingbot.dotty.models.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TickersTradeUpdatesDTO {
    private long tickerTradesId;
    private String name;
    private String symbol;
    private Boolean isActive;
}
