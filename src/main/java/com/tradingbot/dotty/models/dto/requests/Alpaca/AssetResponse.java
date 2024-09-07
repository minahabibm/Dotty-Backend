package com.tradingbot.dotty.models.dto.requests.Alpaca;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradingbot.dotty.utils.constants.alpaca.TradeConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetResponse {

    private String id;
    @JsonProperty("class")
    private TradeConstants class_;
    private TradeConstants exchange;
    private String symbol;
    private String name;
    private TradeConstants status;
    private Boolean tradable;
    private Boolean marginable;
    private Boolean shortable;
    private Boolean easy_to_borrow;
    private Boolean fractionable;
    private Integer maintenance_margin_requirement;
    private String margin_requirement_long;
    private String margin_requirement_short;
    private String[] attributes;

}
