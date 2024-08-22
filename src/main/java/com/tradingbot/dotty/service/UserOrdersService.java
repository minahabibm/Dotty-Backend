package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.UserOrderDTO;

import java.util.List;

public interface UserOrdersService {

    List<UserOrderDTO> getUserOrder();
    Long insertOrder(UserOrderDTO userOrderDTO);
    Long updateUserOrder(UserOrderDTO userOrderDTO);
    String deleteUserOrder();

}
