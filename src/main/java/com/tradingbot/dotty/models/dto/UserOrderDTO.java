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
public class UserOrderDTO {

    private Long userOrderId;
    private String userId;
    private String orderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int version;

}
