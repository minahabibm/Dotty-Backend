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
public class PositionTrackerDTO {

    private Long positionTrackerId;
    private String symbol;
    private String typeOfTrade;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
