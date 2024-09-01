package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.OrdersDTO;

import java.util.List;
import java.util.Optional;

public interface OrdersService {

    List<OrdersDTO> getOrders();
    List<OrdersDTO> getActiveTickerOrders();
    List<OrdersDTO> getOrdersByPositionTracker(Long positionTrackerId);
    Optional<OrdersDTO> getActiveTickerOrder(String symbol);
    Optional<OrdersDTO> insertOrder(OrdersDTO ordersDTO);
    Optional<OrdersDTO> updateOrder(OrdersDTO ordersDTO);
    void deleteOrder();

}
