package com.tradingbot.dotty.models.dto.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketHoursResponse {

    private EventData[] data;
    private String exchange;
    private String timezone;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EventData {
        private String eventName;
        private String atDate;
        private String tradingHour;
    }

}
