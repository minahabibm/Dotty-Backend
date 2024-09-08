package com.tradingbot.dotty.models.dto.requests.FMP;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteOrder {

    private String symbol;
    private String name;
    private Double price;
    private Double changesPercentage;
    private Double change;
    private Double dayLow;
    private Double dayHigh;
    private Double yearHigh;
    private Double yearLow;
    private Long marketCap;
    private Double priceAvg50;
    private Double priceAvg200;
    private String exchange;
    private Double volume;
    private Double avgVolume;
    private Double open;
    private Double previousClose;
    private Double eps;
    private Double pe;
    private String earningsAnnouncement;
    private Long sharesOutstanding;
    private Long timestamp;

}