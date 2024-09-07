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
public class UserConfigurationDTO {

    private Long userConfigurationId;
    private Boolean activeTrading;
    private Boolean isActiveTradingAccount;
    private String alpacaApiKey;
    private String alpacaSecretKey;
    private Boolean alpacaPaperAccount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int version;

}
