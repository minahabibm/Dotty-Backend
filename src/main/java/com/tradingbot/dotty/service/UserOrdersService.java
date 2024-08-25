package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.UserOrderDTO;

import java.util.List;

public interface UserOrdersService {

    List<UserOrderDTO> getUsersOrders();
    Long insertUserOrder(UserOrderDTO userOrderDTO);
    Long updateUserOrder(UserOrderDTO userOrderDTO);
    String deleteUserOrder();

}
