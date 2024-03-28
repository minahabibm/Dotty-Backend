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
public class PositionDTO {

    private Long positionId;
    private String symbol;
    private String tai;
    private Integer taiValue;
    private LocalDateTime intervals;
    private Float open;
    private Float high;
    private Float low;
    private Float close;
    private Long volume;
    private PositionTrackerDTO positionTrackerDTO;
    private LocalDateTime createdOn;

}
