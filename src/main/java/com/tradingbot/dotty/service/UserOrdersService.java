package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.UserOrderDTO;

import java.util.List;
import java.util.Optional;

public interface UserOrdersService {

    List<UserOrderDTO> getUsersOrders();
    Optional<UserOrderDTO> getUserOrder(String alpacaOrderId);
    Optional<UserOrderDTO> insertUserOrder(UserOrderDTO userOrderDTO);
    Optional<UserOrderDTO> updateUserOrder(UserOrderDTO userOrderDTO);
    void deleteUserOrder(Long userOrderId);

}
