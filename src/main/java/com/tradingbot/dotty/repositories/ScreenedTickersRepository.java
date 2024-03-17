package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.ScreenedTicker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScreenedTickersRepository extends JpaRepository<ScreenedTicker, Long> {
    Optional<ScreenedTicker> findBySymbol(String symbol);
}
