package com.tradingbot.dotty.serviceImpls;

import com.tradingbot.dotty.models.UserConfiguration;
import com.tradingbot.dotty.models.dto.UserConfigurationDTO;
import com.tradingbot.dotty.repositories.UserConfigurationRepository;
import com.tradingbot.dotty.service.UserConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tradingbot.dotty.utils.LoggingConstants.*;

@Slf4j
@Service
public class UserConfigurationServiceImpl implements UserConfigurationService {

    @Autowired
    private UserConfigurationRepository userConfigurationRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public List<UserConfigurationDTO> getUsersConfiguration() {
        log.trace(ENTITIES_READ_OPERATION, "User Configuration");
        return userConfigurationRepository.findAll().stream().map(userConfiguration -> modelMapper.map(userConfiguration, UserConfigurationDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Optional<UserConfigurationDTO> getUserConfiguration(Long id) {
        log.trace(ENTITIES_READ_WITH_FILERS_OPERATION, "User Configuration", id);
        Optional userConfiguration = userConfigurationRepository.findById(id);
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
    public String deleteUserConfiguration() {
        return null;
    }
}
