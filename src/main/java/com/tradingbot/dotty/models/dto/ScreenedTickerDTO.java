package com.tradingbot.dotty.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreenedTickerDTO {
    private long id;
    private String name;
    private String symbol;
    private String sector;
    private String exchangeShortName;
    private Float beta;
}
