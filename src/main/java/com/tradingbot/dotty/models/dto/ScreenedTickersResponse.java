package com.tradingbot.dotty.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreenedTickersResponse {
    private String symbol;
    private String companyName;
    private Long marketCap;
    private String sector;
    private String industry;
    private float beta;
    private float price;
    private float lastAnnualDividend;
    private Long volume;
    private String exchange;
    private String exchangeShortName;
    private String country;
    private boolean isEtf;
    private boolean isActivelyTrading;
}
