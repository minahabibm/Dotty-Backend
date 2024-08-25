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
        Optional userConfiguration = userConfigurationRepository.findById(id);
        if(userConfiguration.isPresent())
            return Optional.of(modelMapper.map(userConfiguration.get(), UserConfigurationDTO.class));
        return userConfiguration;
    }

    @Override
    public Optional<UserConfigurationDTO> getUserConfiguration(String loginUid) {
        Optional userConfiguration =  userConfigurationRepository.findUserConfigurationByUsers_LoginUid(loginUid);
        if(userConfiguration.isPresent())
            return Optional.of(modelMapper.map(userConfiguration.get(), UserConfigurationDTO.class));
        return userConfiguration;
    }

    @Override
    public Long insertUserConfiguration(UserConfigurationDTO userConfigurationDTO) {
        log.trace(ENTITY_CREATE_OPERATION, userConfigurationDTO, "User Configuration");
        UserConfiguration userConfiguration = userConfigurationRepository.save(modelMapper.map(userConfigurationDTO, UserConfiguration.class));
        return userConfiguration.getUserConfigurationId();
    }

    @Override
    public Long updateUserConfiguration(UserConfigurationDTO userConfigurationDTO) {
        log.trace(ENTITY_UPDATE_OPERATION, userConfigurationDTO.getUserConfigurationId(), "User Configuration");
        Optional<UserConfiguration> userConfiguration = userConfigurationRepository.findById(userConfigurationDTO.getUserConfigurationId());
        userConfiguration.ifPresent(userConfigurationDAO -> BeanUtils.copyProperties(userConfigurationDTO, userConfigurationDAO, "updatedAt"));
        UserConfiguration userConfigurationUpdated = userConfigurationRepository.save(userConfiguration.get());
        return userConfigurationUpdated.getUserConfigurationId();
    }


    @Override
    public String updateUserTradingAccountAlpaca(UserTradingAccountAlpacaRequest userTradingAccountAlpacaRequest, String loginUid) {
        System.out.println(userTradingAccountAlpacaRequest.getKey() + " " + userTradingAccountAlpacaRequest.getSecret()  + " " + userTradingAccountAlpacaRequest.getPaperAccount());

        if (userTradingAccountAlpacaRequest.getKey() == null || userTradingAccountAlpacaRequest.getSecret() == null || userTradingAccountAlpacaRequest.getKey().isEmpty() || userTradingAccountAlpacaRequest.getSecret().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        userConfigurationRepository.findUserConfigurationByUsers_LoginUid(loginUid).ifPresentOrElse(userConfiguration -> {
            userConfiguration.setAlpacaApiKey(userTradingAccountAlpacaRequest.getKey());
            userConfiguration.setAlpacaSecretKey(userTradingAccountAlpacaRequest.getSecret());
            userConfiguration.setAlpacaPaperAccount(userTradingAccountAlpacaRequest.getPaperAccount());
            userConfiguration.setIsActiveTradingAccount(true);
            updateUserConfiguration(modelMapper.map(userConfiguration, UserConfigurationDTO.class));
        }, () -> {
            System.out.println("User configuration is not present");
            throw new RuntimeException("User configuration not found");
        });
        return "user configuration updated";
    }

    @Override
    public Boolean isUserTradingAccountActive(String loginUid) {
        Optional<UserConfiguration> userConfiguration =  userConfigurationRepository.findUserConfigurationByUsers_LoginUid(loginUid);
        return userConfiguration.filter(configuration -> configuration.getIsActiveTradingAccount() != null ? configuration.getIsActiveTradingAccount() : false).isPresent();
    }

    @Override
    public String deleteUserConfiguration() {
        return null;
    }

}
