package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.PositionTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PositionTrackerRepository extends JpaRepository<PositionTracker, Long> {
    Optional<PositionTracker>  findBySymbolAndActiveTrue(String symbol);

}
