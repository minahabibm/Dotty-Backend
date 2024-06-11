package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.UserConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserConfigurationRepository extends JpaRepository<UserConfiguration, Long> {

}
