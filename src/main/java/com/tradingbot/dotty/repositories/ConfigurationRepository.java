package com.tradingbot.dotty.repositories;

import com.tradingbot.dotty.models.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
    Optional<Configuration> findByNameAndConfigKey(String name, String configKey);
}
