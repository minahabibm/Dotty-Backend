package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findBySymbolAndPositionTracker_PositionTrackerIdOrderByIntervalsDesc(String symbol, Long Id);
    Optional<Position> findBySymbolAndIntervals(String symbol, LocalDateTime interval);

}
