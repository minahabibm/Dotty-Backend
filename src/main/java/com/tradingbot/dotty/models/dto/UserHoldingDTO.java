package com.tradingbot.dotty.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserHoldingDTO {

    private Long userHoldingId;
    private UsersDTO usersDTO;
    private HoldingDTO holdingDTO;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int version;

}
