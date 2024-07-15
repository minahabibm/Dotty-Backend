package com.tradingbot.dotty.models.dto.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

//{
//    "meta": {
//        "symbol": "AAPL",
//        "interval": "5min",
//        "currency": "USD",
//        "exchange_timezone": "America/New_York",
//        "exchange": "NASDAQ",
//        "mic_code": "XNGS",
//        "type": "Common Stock",
//        "indicator": {
//            "name": "RSI - Relative Strength Index",
//            "series_type": "close",
//            "time_period": 14
//            }
//        },
//        "values": [
//            {
//            "datetime": "2024-03-08 15:55:00",
//            "rsi": "29.78143",
//            "open": "171.34000",
//            "high": "171.34500",
//            "low": "170.72000",
//            "close": "170.74001",
//            "volume": "3973678"
//            }
//        ],
//        "status": "ok"
//}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TechnicalIndicatorResponse {

    private MetaDetails meta;
    private List<ValuesDetails> values;
    private String status;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MetaDetails {
        private String symbol;
        private String interval;
        private String currency;
        private String exchange_timezone;
        private String exchange;
        private String mic_code;
        private String type;
        private IndicatorDetails indicator;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IndicatorDetails {
        private String name;
        private String series_type;
        private String time_period;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ValuesDetails {
        private String datetime;
        private String rsi;
        private String open;
        private String high;
        private String low;
        private String close;
        private String volume;
    }
}
