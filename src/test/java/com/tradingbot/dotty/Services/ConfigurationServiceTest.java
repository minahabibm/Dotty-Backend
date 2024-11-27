package com.tradingbot.dotty.Services;

import com.tradingbot.dotty.models.Configuration;
import com.tradingbot.dotty.models.dto.ConfigurationDTO;
import com.tradingbot.dotty.repositories.ConfigurationRepository;
import com.tradingbot.dotty.serviceImpls.ConfigurationServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ConfigurationServiceTest {

    @Autowired
    private ConfigurationServiceImpl configurationService;

    @SpyBean // This creates a spy on the ConfigurationRepository bean
    private ConfigurationRepository configurationRepository;

    @Autowired
    private CacheManager cacheManager;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Cache cache;

    private Configuration configuration;
    private ConfigurationDTO configurationDTO;
    private List<Configuration> configurationList;
    private List<ConfigurationDTO> configurationDTOList;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
        configuration.setConfigurationId(1L);
        configuration.setName("TestConfig");
        configuration.setConfigKey("TestKey");
        configuration.setConfigValue("TestValue");

        configurationDTO = ConfigurationDTO.builder()
                .configurationId(1L)
                .name("TestConfig")
                .configKey("TestKey")
                .configValue("TestValue")
                .build();

        configurationList = List.of(configuration);
        configurationDTOList = List.of(configurationDTO);

        lenient().when(modelMapper.map(configuration, ConfigurationDTO.class)).thenReturn(configurationDTO);
        lenient().when(modelMapper.map(configurationDTO, Configuration.class)).thenReturn(configuration);
    }

    @AfterEach
    void tearDown() {
        // Resetting or clearing mock states or caches
        if (cacheManager.getCache("configurations") != null) {
            cacheManager.getCache("configurations").clear();  // Clear cache after each test
        }

        // Reset mocked data if necessary
        reset(configurationRepository, modelMapper);  // Resets all mock interactions for a clean slate
    }

    @Test
    void testGetConfigurations() {
        when(configurationRepository.findAll()).thenReturn(configurationList);

        // First call should interact with the repository
        List<ConfigurationDTO> result1 = configurationService.getConfigurations();
        assertEquals(1, result1.size());
        assertEquals(configurationDTO, result1.get(0));
        verify(configurationRepository, times(1)).findAll(); // Ensure repository is called once

        // Following calls should hit the cache, no repository interaction
        for (int i = 0; i < 10; i++) {
            List<ConfigurationDTO> result2 = configurationService.getConfigurations();
            assertEquals(1, result2.size());
            assertEquals(configurationDTO, result2.get(0));
            verify(configurationRepository, times(1)).findAll(); // No additional calls to the repository
        }
    }

    @Test
    void testGetConfigurationByNameAndKey() {
        when(configurationRepository.findByNameAndConfigKey("TestConfig", "TestKey")).thenReturn(Optional.of(configuration));

        // First call should interact with the repository
        Optional<ConfigurationDTO> result1 = configurationService.getConfigurationByNameAndKey("TestConfig", "TestKey");
        assertTrue(result1.isPresent());
        assertEquals(configurationDTO, result1.get());
        verify(configurationRepository, times(1)).findByNameAndConfigKey("TestConfig", "TestKey");

        // Following call should hit the cache
        for (int i = 0; i < 10; i++) {
            Optional<ConfigurationDTO> result2 = configurationService.getConfigurationByNameAndKey("TestConfig", "TestKey");
            assertTrue(result2.isPresent());
            assertEquals(configurationDTO, result2.get());
            verify(configurationRepository, times(1)).findByNameAndConfigKey("TestConfig", "TestKey"); // No additional calls
        }
    }

    @Test
    void testInsertConfiguration() {
        // Arrange
        when(configurationRepository.findAll()).thenReturn(configurationList);
        when(configurationRepository.save(configuration)).thenReturn(configuration);

        // First call to getConfigurations() should populate the cache
        List<ConfigurationDTO> result1 = configurationService.getConfigurations();
        assertEquals(1, result1.size(), "Result should contain one configuration");
        assertEquals(configurationDTO, result1.get(0), "Returned configuration should match the DTO");
        verify(configurationRepository, times(1)).findAll(); // Ensure repository is called once

        // Subsequent calls should not query the repository again
        for (int i = 0; i < 5; i++) {
            List<ConfigurationDTO> cachedResult = configurationService.getConfigurations();
            assertEquals(1, cachedResult.size(), "Result should still contain one configuration");
            assertEquals(configurationDTO, cachedResult.get(0), "Cached configuration should match the DTO");
        }
        verify(configurationRepository, times(1)).findAll(); // Repository is still only called once

        // Act: Insert a new configuration
        configurationService.insertConfiguration(configurationDTO);

        // Cache should be invalidated, and subsequent call to getConfigurations() should query the repository
        List<ConfigurationDTO> resultAfterInsert = configurationService.getConfigurations();
        assertEquals(1, resultAfterInsert.size(), "Result should contain the new configuration");
        assertEquals(configurationDTO, resultAfterInsert.get(0), "Returned configuration should match the new DTO");

        // Verify repository interactions
        verify(configurationRepository, times(2)).findAll(); // Repository should be queried again after cache invalidation
        verify(configurationRepository, times(1)).save(configuration); // Ensure insert called save
    }

    @Test
    void testUpdateConfiguration() {
        when(configurationRepository.findByNameAndConfigKey("TestConfig", "TestKey")).thenReturn(Optional.of(configuration));
        when(configurationRepository.save(configuration)).thenReturn(configuration);


        Cache cache = cacheManager.getCache("configurations");
        assertNotNull(cache, "Cache should not be null");
        cache.evict("TestConfig-TestKey");  // Evict the cache entry for the key
        assertNull(cache.get("TestConfig-TestKey"), "Cache should be empty before repository interaction");


        // First call to getConfigurationByNameAndKey should interact with the repository and populate the cache
        Optional<ConfigurationDTO> result1 = configurationService.getConfigurationByNameAndKey("TestConfig", "TestKey");
        assertTrue(result1.isPresent(), "Result should be present");
        assertEquals(configurationDTO, result1.get(), "Returned DTO should match the configuration DTO");
        verify(configurationRepository, times(1)).findByNameAndConfigKey("TestConfig", "TestKey");

        // Subsequent calls should hit the cache
        for (int i = 0; i < 5; i++) {
            Optional<ConfigurationDTO> cachedResult = configurationService.getConfigurationByNameAndKey("TestConfig", "TestKey");
            assertTrue(cachedResult.isPresent(), "Cached result should be present");
            assertEquals(configurationDTO, cachedResult.get(), "Cached DTO should match the configuration DTO");
        }
        verify(configurationRepository, times(1)).findByNameAndConfigKey("TestConfig", "TestKey"); // Repository is still called only once

        // Act: Update the configuration
        Optional<ConfigurationDTO> updatedResult = configurationService.updateConfiguration(configurationDTO);

        // Assert: Validate the update operation
        assertTrue(updatedResult.isPresent(), "Update result should be present");
        assertEquals(configurationDTO, updatedResult.get(), "Updated DTO should match the configuration DTO");
        verify(configurationRepository, times(1)).findById(configurationDTO.getConfigurationId());
        verify(configurationRepository, times(1)).save(configuration);

        // Cache eviction validation: After update, cache should be cleared
        Optional<ConfigurationDTO> resultAfterUpdate = configurationService.getConfigurationByNameAndKey("TestConfig", "TestKey");
        assertTrue(resultAfterUpdate.isPresent(), "Result after update should be present");
        assertEquals(configurationDTO, resultAfterUpdate.get(), "Result after update should match the updated configuration DTO");

        // Ensure repository is called again because the cache was invalidated
        verify(configurationRepository, times(2)).findByNameAndConfigKey("TestConfig", "TestKey");
    }

    @Test
    void testUpdateConfigurationThrowsException() {
        ConfigurationDTO invalidDTO = ConfigurationDTO.builder().name("Invalid").build();

        assertThrows(RuntimeException.class, () -> configurationService.updateConfiguration(invalidDTO));
        verify(configurationRepository, never()).findById(anyLong());
        verify(configurationRepository, never()).save(any());
    }

    @Test
    void testDeleteConfiguration() {
        configurationService.deleteConfiguration();
        // No operations to verify since the method is empty
//        verifyNoInteractions(configurationRepository);
//        verifyNoInteractions(modelMapper);
    }

}
