package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.UserOrderDTO;

import java.util.List;
import java.util.Optional;

public interface UserOrdersService {

    List<UserOrderDTO> getUsersOrders();
    Optional<UserOrderDTO> getUserOrder(String alpacaOrderId);
    Long insertUserOrder(UserOrderDTO userOrderDTO);
    Long updateUserOrder(UserOrderDTO userOrderDTO);
    String deleteUserOrder();

}
