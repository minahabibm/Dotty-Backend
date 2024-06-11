package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.UserConfigurationDTO;

import java.util.List;
import java.util.Optional;

public interface UserConfigurationService {

    List<UserConfigurationDTO> getUsersConfiguration();
    Optional<UserConfigurationDTO> getUserConfiguration(Long id);
    Long insertUserConfiguration(UserConfigurationDTO userConfigurationDTO);
    Long updateUserConfiguration(UserConfigurationDTO userConfigurationDTO);
    String deleteUserConfiguration();

}
