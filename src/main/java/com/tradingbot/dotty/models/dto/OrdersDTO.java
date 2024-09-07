package com.tradingbot.dotty.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrdersDTO {

    private Long orderTickerId;
    private String symbol;
    private String tradeType;
    private Long quantity;
    private Float entryPrice;
    private LocalDateTime entryTime;
    private Boolean active;
    private PositionTrackerDTO positionTrackerDTO;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
