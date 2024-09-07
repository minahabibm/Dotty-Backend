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
public class UserOrderDTO {

    private Long userOrderId;
    private String alpacaOrderId;
    private String filled;
    private Double filledQty;
    private Double filledAvgPrice;
    private UsersDTO usersDTO;
    private OrdersDTO ordersDTO;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int version;

}
