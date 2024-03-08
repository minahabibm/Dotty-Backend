package com.tradingbot.dotty.models.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;

import java.util.List;
//{
//    "data":
//        [
//                {"c":["24","12"],"p":207.03,"s":"IWM","t":1709850723750,"v":33},
//                {"c":["24"],"p":207.04,"s":"IWM","t":1709850723750,"v":100},
//                {"c":["24","12"],"p":207.04,"s":"IWM","t":1709850723750,"v":10}
//        ],
//        "type":"trade"
//}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TickersUpdateWSDTO {
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
