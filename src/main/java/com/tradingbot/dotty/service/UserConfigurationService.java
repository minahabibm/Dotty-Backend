package com.tradingbot.dotty.service;

import com.tradingbot.dotty.models.dto.UserConfigurationDTO;
import com.tradingbot.dotty.models.dto.requests.UserTradingAccountAlpacaRequest;

import java.util.List;
import java.util.Optional;

public interface UserConfigurationService {

    List<UserConfigurationDTO> getUsersConfiguration();
    Optional<UserConfigurationDTO> getUserConfiguration(Long id);
    Optional<UserConfigurationDTO> getUserConfiguration(String loginUid);
    Long insertUserConfiguration(UserConfigurationDTO userConfigurationDTO);
    Long updateUserConfiguration(UserConfigurationDTO userConfigurationDTO);
    String updateUserTradingAccountAlpaca(UserTradingAccountAlpacaRequest userTradingAccountAlpacaRequest, String loginUid);
    Boolean isUserTradingAccountActive(String loginUid);
    String deleteUserConfiguration();

}
