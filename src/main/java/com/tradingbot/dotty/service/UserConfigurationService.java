package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.UserConfigurationDTO;
import com.tradingbot.dotty.models.dto.requests.UserTradingAccountAlpacaRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserConfigurationService {

    List<UserConfigurationDTO> getUsersConfigurations();
    List<UserConfigurationDTO> getUsersConfigurationsWithActiveTradingAccounts();
    Optional<UserConfigurationDTO> getUserConfiguration(Long userConfigurationId);
    Optional<UserConfigurationDTO> getUserConfiguration(String loginUid);
    Optional<UserConfigurationDTO> insertUserConfiguration(UserConfigurationDTO userConfigurationDTO);
    Optional<UserConfigurationDTO> updateUserConfiguration(UserConfigurationDTO userConfigurationDTO);
    Optional<UserConfigurationDTO> updateUserTradingAccountAlpaca(UserTradingAccountAlpacaRequest userTradingAccountAlpacaRequest, String loginUid);
    void deleteUserConfiguration(Long userConfigurationId);

    Map<String, Boolean> isUserTradingAccountActive();

}
