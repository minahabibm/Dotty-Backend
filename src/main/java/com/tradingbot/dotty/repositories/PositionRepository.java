package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findBySymbolAndPositionTracker_PositionTrackerIdOrderByIntervalsDesc(String symbol, Long Id);

}
