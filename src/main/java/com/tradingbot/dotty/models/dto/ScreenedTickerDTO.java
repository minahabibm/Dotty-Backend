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
public class ScreenedTickerDTO {

    private Long screenedTickerId;
    private String name;
    private String symbol;
    private String sector;
    private String exchangeShortName;
    private Float beta;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
