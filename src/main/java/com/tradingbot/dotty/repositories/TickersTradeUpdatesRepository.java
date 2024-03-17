package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.TickersTradeUpdates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TickersTradeUpdatesRepository extends JpaRepository<TickersTradeUpdates, Long> {
    Optional<TickersTradeUpdates> findBySymbol(String symbol);
}
