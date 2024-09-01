package com.tradingbot.dotty.serviceImpls;

import com.tradingbot.dotty.models.UserConfiguration;
import com.tradingbot.dotty.models.dto.UserConfigurationDTO;
import com.tradingbot.dotty.models.dto.requests.UserTradingAccountAlpacaRequest;
import com.tradingbot.dotty.repositories.UserConfigurationRepository;
import com.tradingbot.dotty.service.UserConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;

@Slf4j
@Service
public class UserConfigurationServiceImpl implements UserConfigurationService {

    @Autowired
    private UserConfigurationRepository userConfigurationRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public List<UserConfigurationDTO> getUsersConfigurations() {
        log.trace(ENTITIES_READ_OPERATION, "User Configuration");
        return userConfigurationRepository.findAll().stream().map(userConfiguration -> modelMapper.map(userConfiguration, UserConfigurationDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<UserConfigurationDTO> getUsersConfigurationsWithActiveTradingAccounts() {
        log.trace(ENTITIES_READ_WITH_FILTERS_OPERATION, "User Configuration", "active trading accounts");
        return userConfigurationRepository.findUserConfigurationByIsActiveTradingAccountTrue().stream().map(userConfiguration -> modelMapper.map(userConfiguration, UserConfigurationDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Optional<UserConfigurationDTO> getUserConfiguration(Long id) {
        log.trace(ENTITIES_READ_WITH_FILTERS_OPERATION, "User Configuration", id);
        return userConfigurationRepository.findById(id).map(userConfiguration -> modelMapper.map(userConfiguration, UserConfigurationDTO.class));
    }

    @Override
    public Optional<UserConfigurationDTO> getUserConfiguration(String loginUid) {
        log.trace(ENTITIES_READ_WITH_FILTERS_OPERATION, "User Configuration", loginUid);
        return userConfigurationRepository.findUserConfigurationByUsers_LoginUid(loginUid).map(userConfiguration -> modelMapper.map(userConfiguration, UserConfigurationDTO.class));
    }

    @Override
    public Optional<UserConfigurationDTO> insertUserConfiguration(UserConfigurationDTO userConfigurationDTO) {
        log.trace(ENTITY_CREATE_OPERATION, userConfigurationDTO, "User Configuration");
        UserConfiguration userConfiguration = userConfigurationRepository.save(modelMapper.map(userConfigurationDTO, UserConfiguration.class));
        return Optional.of(modelMapper.map(userConfiguration, UserConfigurationDTO.class));
    }

    @Override
    public Optional<UserConfigurationDTO> updateUserConfiguration(UserConfigurationDTO userConfigurationDTO) {
        log.trace(ENTITY_UPDATE_OPERATION, userConfigurationDTO.getUserConfigurationId(), "User Configuration");
        return userConfigurationRepository.findById(userConfigurationDTO.getUserConfigurationId())
                .map(existingUserConfiguration -> {
                    BeanUtils.copyProperties(userConfigurationDTO, existingUserConfiguration, "updatedAt");
                    UserConfiguration updatedUserConfiguration = userConfigurationRepository.save(existingUserConfiguration);
                    return modelMapper.map(updatedUserConfiguration, UserConfigurationDTO.class);
                });
    }


    @Override
    public Optional<UserConfigurationDTO> updateUserTradingAccountAlpaca(UserTradingAccountAlpacaRequest userTradingAccountAlpacaRequest, String loginUid) {
        log.trace(ENTITY_UPDATE_OPERATION, loginUid, "User Configuration");
        if (userTradingAccountAlpacaRequest.getKey() == null || userTradingAccountAlpacaRequest.getSecret() == null || userTradingAccountAlpacaRequest.getKey().isEmpty() || userTradingAccountAlpacaRequest.getSecret().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        return userConfigurationRepository.findUserConfigurationByUsers_LoginUid(loginUid)
                .map(existingUserConfiguration -> {
                    existingUserConfiguration.setAlpacaApiKey(userTradingAccountAlpacaRequest.getKey());
                    existingUserConfiguration.setAlpacaSecretKey(userTradingAccountAlpacaRequest.getSecret());
                    existingUserConfiguration.setAlpacaPaperAccount(userTradingAccountAlpacaRequest.getPaperAccount());
                    existingUserConfiguration.setIsActiveTradingAccount(true);
                    return updateUserConfiguration(modelMapper.map(existingUserConfiguration, UserConfigurationDTO.class));
                }).orElseThrow(() -> new RuntimeException("User configuration not found"));
    }

    @Override
    public Boolean isUserTradingAccountActive(String loginUid) {
        log.trace(ENTITIES_READ_WITH_FILTERS_OPERATION, "User Configuration", loginUid);
        Optional<UserConfiguration> userConfiguration =  userConfigurationRepository.findUserConfigurationByUsers_LoginUid(loginUid);
        return userConfiguration.filter(configuration -> configuration.getIsActiveTradingAccount() != null ? configuration.getIsActiveTradingAccount() : false).isPresent();
    }

    @Override
    public void deleteUserConfiguration(Long userConfigurationId) {
        log.trace(ENTITY_DELETE_OPERATION, userConfigurationId, "User Configuration");
        UserConfiguration userConfiguration = userConfigurationRepository.findById(userConfigurationId).orElseThrow(() -> new IllegalArgumentException("User Configuration not found"));
        userConfigurationRepository.delete(userConfiguration);
    }

}
