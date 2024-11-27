package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.ConfigurationDTO;

import java.util.List;
import java.util.Optional;

public interface ConfigurationService {

    List<ConfigurationDTO> getConfigurations();
    Optional<ConfigurationDTO> getConfigurationByNameAndKey(String name, String key);
    Optional<ConfigurationDTO> insertConfiguration(ConfigurationDTO configurationDTO);
    Optional<ConfigurationDTO> updateConfiguration(ConfigurationDTO configurationDTO);
    void deleteConfiguration();

}
