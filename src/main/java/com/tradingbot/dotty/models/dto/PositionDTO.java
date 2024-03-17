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

    private long positionId;
    private String symbol;
    private String tai;
    private String taiValue;
    private LocalDateTime intervals;
    private String open;
    private String high;
    private String low;
    private String close;
    private Long volume;
    private PositionTrackerDTO positionTrackerDTO;
    private LocalDateTime createdOn;

}
