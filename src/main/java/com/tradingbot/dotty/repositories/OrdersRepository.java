package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findBySymbolAndActiveTrue(String symbol);

}
