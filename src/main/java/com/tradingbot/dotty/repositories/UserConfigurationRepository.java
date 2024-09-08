package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.UserConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserConfigurationRepository extends JpaRepository<UserConfiguration, Long> {

    List<UserConfiguration> findUserConfigurationByIsActiveTradingAccountTrue();
    Optional<UserConfiguration> findUserConfigurationByUsers_LoginUid(String loginUid);

}
