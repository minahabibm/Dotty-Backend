package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.TickersTradeUpdates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TickersTradeUpdatesRepository extends JpaRepository<TickersTradeUpdates, Long> {
}
