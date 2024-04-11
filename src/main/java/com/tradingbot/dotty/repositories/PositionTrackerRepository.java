package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.PositionTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionTrackerRepository extends JpaRepository<PositionTracker, Long> {
    PositionTracker findBySymbolAndActiveTrue(String symbol);

}
