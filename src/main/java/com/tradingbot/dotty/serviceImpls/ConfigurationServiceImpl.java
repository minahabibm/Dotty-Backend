package com.tradingbot.dotty.serviceImpls;

import com.tradingbot.dotty.models.Configuration;
import com.tradingbot.dotty.models.dto.ConfigurationDTO;
import com.tradingbot.dotty.repositories.ConfigurationRepository;
import com.tradingbot.dotty.service.ConfigurationService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tradingbot.dotty.utils.constants.Constants.DATA_INITIALIZATION;
import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;

@Slf4j
@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    @Cacheable(value = "configurations")
    public List<ConfigurationDTO> getConfigurations() {
        log.trace(ENTITIES_READ_OPERATION, "Configuration");
        return configurationRepository.findAll().stream().map(configuration -> modelMapper.map(configuration, ConfigurationDTO.class)).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "configurations", key = "#name.concat('-').concat(#key)")
    public Optional<ConfigurationDTO> getConfigurationByNameAndKey(String name, String key) {
        log.trace(ENTITIES_READ_WITH_FILTERS_OPERATION, "Configuration", "Name: "+ name +" and Key: " + key);
        return configurationRepository.findByNameAndConfigKey(name, key).map(configuration -> modelMapper.map(configuration, ConfigurationDTO.class));
    }

    @Override
    @CacheEvict(value = "configurations", allEntries = true)
    public Optional<ConfigurationDTO> insertConfiguration(ConfigurationDTO configurationDTO) {
        log.trace(ENTITY_CREATE_OPERATION, configurationDTO, "Configuration");
        Configuration configuration = configurationRepository.save(modelMapper.map(configurationDTO, Configuration.class));
        return Optional.of(modelMapper.map(configuration, ConfigurationDTO.class));
    }

    @Override
    @CacheEvict(value = "configurations", allEntries = true)
    public Optional<ConfigurationDTO> updateConfiguration(ConfigurationDTO configurationDTO) {
        log.trace(ENTITY_UPDATE_OPERATION, configurationDTO.getName(), "Configuration");
        if(configurationDTO.getConfigurationId() == null)
            throw new RuntimeException();
        return configurationRepository.findById(configurationDTO.getConfigurationId())
                .map(existingConfiguration -> {
                    BeanUtils.copyProperties(configurationDTO, existingConfiguration, "Configuration");
                    Configuration updatedConfiguration = configurationRepository.save(existingConfiguration);
                    return modelMapper.map(updatedConfiguration, ConfigurationDTO.class);
                });
    }

    @Override
    @CacheEvict(value = "configurations", allEntries = true)
    public void deleteConfiguration() {

    }


    @PostConstruct
    public void init() {
        configurationRepository.saveAll(DATA_INITIALIZATION);
        log.info("Initial configuration data inserted into the database.");
    }

}
