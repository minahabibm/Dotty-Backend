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
public class PositionDTO {

    private Long positionId;
    private String symbol;
    private String tai;
    private Float taiValue;
    private LocalDateTime intervals;
    private Float open;
    private Float high;
    private Float low;
    private Float close;
    private Long volume;
    private PositionTrackerDTO positionTrackerDTO;
    private LocalDateTime createdOn;

}
