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
public class UserHoldingDTO {

    private Long userHoldingId;
    private UsersDTO usersDTO;
    private HoldingDTO holdingDTO;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int version;

}
