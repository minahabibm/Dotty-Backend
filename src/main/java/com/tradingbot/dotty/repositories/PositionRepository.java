package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.Position;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findAllBySymbolAndPositionTracker_ActiveTrue(String symbol);

}
