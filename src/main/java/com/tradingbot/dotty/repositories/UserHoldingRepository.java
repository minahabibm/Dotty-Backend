package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.UserHolding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHoldingRepository extends JpaRepository<UserHolding, Long> {

}
