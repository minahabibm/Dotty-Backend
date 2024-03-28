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
public class OrdersDTO {

    private Long orderTickerId;
    private String symbol;
    private String tradeType;
    private Long quantity;
    private Float entryPrice;
    private Boolean active;
    private PositionTrackerDTO positionTrackerDTO;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
