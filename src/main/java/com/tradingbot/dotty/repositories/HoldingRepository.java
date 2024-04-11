package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {
}
