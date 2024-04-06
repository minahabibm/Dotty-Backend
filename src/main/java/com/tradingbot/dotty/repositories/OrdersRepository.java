package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByPositionTracker_PositionTrackerIdOrderByCreatedAtAsc(Long positionTrackerId);
    List<Orders> findAllByActiveTrue();
    Optional<Orders> findBySymbolAndActiveTrue(String symbol);

}
