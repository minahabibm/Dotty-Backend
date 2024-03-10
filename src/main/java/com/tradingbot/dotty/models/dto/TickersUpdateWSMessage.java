package com.tradingbot.dotty.models.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TickersUpdateWSMessage {
    private List<TradeDetails> data;
    private String type;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TradeDetails {
        private List<String> c;
        private float p;
        private String s;
        private Long t;
        private Long v;
    }
}
