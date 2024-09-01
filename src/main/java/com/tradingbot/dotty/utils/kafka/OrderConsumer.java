package com.tradingbot.dotty.utils.kafka;

import com.tradingbot.dotty.service.handler.OrderProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

//    @Autowired
//    private OrderProcessingService orderProcessingService;
//
//    @KafkaListener(topics = "entry-orders", groupId = "order-consumer-group")
//    public void toEntryOrder(String order) {
//        System.out.println(order);
//        orderProcessingService.enterPosition(order);;
//    }
//
//    @KafkaListener(topics = "exit-orders", groupId = "order-consumer-group")
//    public void toExitOrder(String order) {
//        System.out.println(order);
//        orderProcessingService.exitPosition(order);
//    }

}
