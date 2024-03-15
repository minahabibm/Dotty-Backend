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
public class PositionTrackerDTO {

    private Long positionTrackerID;
    private String symbol;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
