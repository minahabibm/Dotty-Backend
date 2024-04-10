package com.tradingbot.dotty.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TickersTradeUpdatesDTO {
    private Long tickerTradesId;
    private String name;
    private String symbol;
    private Boolean isActive;
    private ScreenedTickerDTO screenedTickerDTO;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
