package com.tradingbot.dotty.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserConfigurationDTO {

    private Long userConfigurationId;
    private Boolean activeTrading;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int version;

}
