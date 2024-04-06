package com.tradingbot.dotty.controllers;

import com.tradingbot.dotty.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @GetMapping("/activeTickers")
    public ResponseEntity<?> getActiveOrders(){
        return ResponseEntity.ok(ordersService.getActiveTickerOrders());
    }

}
