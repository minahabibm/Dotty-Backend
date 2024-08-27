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
    private String alpacaOrderId;
    private String filledQty;
    private String filledAvgPrice;
    private UsersDTO usersDTO;
    private OrdersDTO ordersDTO;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int version;

}
