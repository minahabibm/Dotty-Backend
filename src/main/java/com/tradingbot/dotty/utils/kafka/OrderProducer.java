package com.tradingbot.dotty.utils.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendEntryOrder(String message) {
        kafkaTemplate.send("entry-orders", message);
    }

    public void sendExitOrder(String message) {
        kafkaTemplate.send("exit-orders", message);
    }

}
