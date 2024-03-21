package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.OrdersDTO;

import java.util.List;

public interface OrdersService {

    List<OrdersDTO> getOrders();
    OrdersDTO getActiveTickerOrder(String symbol);
    List<Long> insertOrders(List<OrdersDTO> ordersDTOList);
    Long insertOrder(OrdersDTO ordersDTO);
    Long updateOrder(OrdersDTO ordersDTO);
    String deleteOrder();

}
