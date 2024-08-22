package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOrdersRepository extends JpaRepository<UserOrder, Long> {

}
