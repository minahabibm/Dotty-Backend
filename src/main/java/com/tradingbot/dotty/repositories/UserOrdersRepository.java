package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOrdersRepository extends JpaRepository<UserOrder, Long> {
    Optional<UserOrder> findByAlpacaOrderId(String alpacaOrderId);
}
