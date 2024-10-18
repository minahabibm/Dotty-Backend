package com.tradingbot.dotty.Services;

import com.tradingbot.dotty.models.UserConfiguration;
import com.tradingbot.dotty.models.dto.UserConfigurationDTO;
import com.tradingbot.dotty.models.dto.requests.UserTradingAccountAlpacaRequest;
import com.tradingbot.dotty.repositories.UserConfigurationRepository;
import com.tradingbot.dotty.service.handler.AuthService;
import com.tradingbot.dotty.serviceImpls.UserConfigurationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserConfigurationServiceTest {

    @InjectMocks
    private UserConfigurationServiceImpl userConfigurationService;

    @Mock
    private UserConfigurationRepository userConfigurationRepository;

    @Mock
    private AuthService authService;

    @Mock
    private JwtAuthenticationToken jwtAuthenticationToken;

    @Mock
    private ModelMapper modelMapper;

    private UserConfiguration userConfiguration;
    private UserConfigurationDTO userConfigurationDTO;
    private UserConfiguration updatedUserConfiguration;
    private UserConfigurationDTO updatedUserConfigurationDTO;

    @BeforeEach
    void setUp() {
        userConfiguration = new UserConfiguration();
        userConfiguration.setUserConfigurationId(1L);
        userConfiguration.setAlpacaApiKey("apiKey");
        userConfiguration.setAlpacaSecretKey("secretKey");
        userConfiguration.setAlpacaPaperAccount(true);
        userConfiguration.setIsActiveTradingAccount(true);

        userConfigurationDTO = new UserConfigurationDTO();
        userConfigurationDTO.setUserConfigurationId(1L);
        userConfigurationDTO.setAlpacaApiKey("apiKey");
        userConfigurationDTO.setAlpacaSecretKey("secretKey");
        userConfigurationDTO.setAlpacaPaperAccount(true);
        userConfigurationDTO.setIsActiveTradingAccount(true);

        updatedUserConfiguration = new UserConfiguration();
        updatedUserConfiguration.setUserConfigurationId(1L);
        updatedUserConfiguration.setAlpacaApiKey("updatedApiKey");
        updatedUserConfiguration.setAlpacaSecretKey("updatedSecretKey");
        updatedUserConfiguration.setAlpacaPaperAccount(true);
        updatedUserConfiguration.setIsActiveTradingAccount(true);

        updatedUserConfigurationDTO = new UserConfigurationDTO();
        updatedUserConfigurationDTO.setUserConfigurationId(1L);
        updatedUserConfigurationDTO.setAlpacaApiKey("updatedApiKey");
        updatedUserConfigurationDTO.setAlpacaSecretKey("updatedSecretKey");
        updatedUserConfigurationDTO.setAlpacaPaperAccount(true);
        updatedUserConfigurationDTO.setIsActiveTradingAccount(true);



        // Lenient stubbing for modelMapper
        lenient().when(modelMapper.map(userConfiguration, UserConfigurationDTO.class)).thenReturn(userConfigurationDTO);
        lenient().when(modelMapper.map(userConfigurationDTO, UserConfiguration.class)).thenReturn(userConfiguration);
        lenient().when(modelMapper.map(updatedUserConfiguration, UserConfigurationDTO.class)).thenReturn(updatedUserConfigurationDTO);
        lenient().when(modelMapper.map(updatedUserConfigurationDTO, UserConfiguration.class)).thenReturn(updatedUserConfiguration);


        // Mocking authService and JwtAuthenticationToken behavior
        lenient().when(authService.getAuthenticatedUser()).thenReturn(jwtAuthenticationToken);
        lenient().when(jwtAuthenticationToken.getName()).thenReturn("testLoginUid");  // Mock getName() to return a sample login UID
    }

    @Test
    void testGetUsersConfigurations() {
        // Arrange
        List<UserConfiguration> userConfigurations = Arrays.asList(userConfiguration);
        List<UserConfigurationDTO> userConfigurationDTOs = Arrays.asList(userConfigurationDTO);

        when(userConfigurationRepository.findAll()).thenReturn(userConfigurations);

        // Act
        List<UserConfigurationDTO> result = userConfigurationService.getUsersConfigurations();

        // Assert
        assertEquals(userConfigurationDTOs, result);
        verify(userConfigurationRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(userConfiguration, UserConfigurationDTO.class);
    }

    @Test
    void testGetUsersConfigurationsWithActiveTradingAccounts() {
        // Arrange
        List<UserConfiguration> userConfigurations = Arrays.asList(userConfiguration);
        List<UserConfigurationDTO> userConfigurationDTOs = Arrays.asList(userConfigurationDTO);

        when(userConfigurationRepository.findUserConfigurationByIsActiveTradingAccountTrue()).thenReturn(userConfigurations);

        // Act
        List<UserConfigurationDTO> result = userConfigurationService.getUsersConfigurationsWithActiveTradingAccounts();

        // Assert
        assertEquals(userConfigurationDTOs, result);
        verify(userConfigurationRepository, times(1)).findUserConfigurationByIsActiveTradingAccountTrue();
        verify(modelMapper, times(1)).map(userConfiguration, UserConfigurationDTO.class);
    }

    @Test
    void testGetUserConfigurationById() {
        // Arrange
        Long id = 1L;
        when(userConfigurationRepository.findById(id)).thenReturn(Optional.of(userConfiguration));

        // Act
        Optional<UserConfigurationDTO> result = userConfigurationService.getUserConfiguration(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userConfigurationDTO, result.get());
        verify(userConfigurationRepository, times(1)).findById(id);
        verify(modelMapper, times(1)).map(userConfiguration, UserConfigurationDTO.class);
    }

    @Test
    void testGetUserConfigurationByLoginUid() {
        // Arrange
        String loginUid = "testLoginUid";
        when(userConfigurationRepository.findUserConfigurationByUsers_LoginUid(loginUid)).thenReturn(Optional.of(userConfiguration));

        // Act
        Optional<UserConfigurationDTO> result = userConfigurationService.getUserConfiguration(loginUid);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userConfigurationDTO, result.get());
        verify(userConfigurationRepository, times(1)).findUserConfigurationByUsers_LoginUid(loginUid);
        verify(modelMapper, times(1)).map(userConfiguration, UserConfigurationDTO.class);
    }

    @Test
    void testGetUsersConfigurations_Empty() {
        // Arrange
        when(userConfigurationRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<UserConfigurationDTO> result = userConfigurationService.getUsersConfigurations();

        // Assert
        assertTrue(result.isEmpty());
        verify(userConfigurationRepository, times(1)).findAll();
    }

    @Test
    void testInsertUserConfiguration() {
        // Arrange
        when(userConfigurationRepository.save(any(UserConfiguration.class))).thenReturn(userConfiguration);
        when(modelMapper.map(userConfiguration, UserConfigurationDTO.class)).thenReturn(userConfigurationDTO);

        // Act
        Optional<UserConfigurationDTO> result = userConfigurationService.insertUserConfiguration(userConfigurationDTO);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userConfigurationDTO, result.get());
        verify(modelMapper, times(1)).map(userConfigurationDTO, UserConfiguration.class);
        verify(userConfigurationRepository, times(1)).save(userConfiguration);
        verify(modelMapper, times(1)).map(userConfiguration, UserConfigurationDTO.class);
    }

    @Test
    void testInsertUserConfiguration_Exception() {
        // Arrange
        when(userConfigurationRepository.save(any(UserConfiguration.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userConfigurationService.insertUserConfiguration(userConfigurationDTO));
        verify(userConfigurationRepository, times(1)).save(any(UserConfiguration.class));
    }


    @Test
    void testUpdateUserConfiguration() {
        // Arrange
        Long id = 1L;
        userConfigurationDTO.setUserConfigurationId(id);

        when(userConfigurationRepository.findById(id)).thenReturn(Optional.of(userConfiguration));
        when(userConfigurationRepository.save(userConfiguration)).thenReturn(updatedUserConfiguration);

        // Act
        Optional<UserConfigurationDTO> result = userConfigurationService.updateUserConfiguration(userConfigurationDTO);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(updatedUserConfigurationDTO, result.get());
        verify(userConfigurationRepository, times(1)).findById(id);
        verify(userConfigurationRepository, times(1)).save(userConfiguration);
        verify(modelMapper, times(1)).map(updatedUserConfiguration, UserConfigurationDTO.class);
    }

    @Test
    void testUpdateUserTradingAccountAlpaca_Success() {
        // Arrange
        String loginUid = "testLoginUid";
        Long id = 1L;
        UserTradingAccountAlpacaRequest request = new UserTradingAccountAlpacaRequest("apiKey", "secretKey", true);

        when(userConfigurationRepository.findUserConfigurationByUsers_LoginUid(loginUid)).thenReturn(Optional.of(userConfiguration));
        when(userConfigurationRepository.save(userConfiguration)).thenReturn(updatedUserConfiguration);
        when(userConfigurationRepository.findById(id)).thenReturn(Optional.of(userConfiguration));
        when(userConfigurationRepository.save(userConfiguration)).thenReturn(updatedUserConfiguration);


        // Act
        Optional<UserConfigurationDTO> result = userConfigurationService.updateUserTradingAccountAlpaca(request, loginUid);

        System.out.println(result);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(updatedUserConfigurationDTO, result.get());
        assertEquals("apiKey", userConfiguration.getAlpacaApiKey());
        assertEquals("secretKey", userConfiguration.getAlpacaSecretKey());
        assertEquals(true, userConfiguration.getAlpacaPaperAccount());
        assertTrue(userConfiguration.getIsActiveTradingAccount());

        // Verify the repository interaction
        verify(userConfigurationRepository, times(1)).findUserConfigurationByUsers_LoginUid(loginUid);
        verify(userConfigurationRepository, times(1)).save(userConfiguration);
    }

    @Test
    void testUpdateUserTradingAccountAlpaca_UserConfigurationNotFound() {
        // Arrange
        String loginUid = "testLoginUid";
        UserTradingAccountAlpacaRequest request = new UserTradingAccountAlpacaRequest("apiKey", "secretKey", true);

        // Mock the repository to return an empty Optional (user not found)
        when(userConfigurationRepository.findUserConfigurationByUsers_LoginUid(loginUid)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                userConfigurationService.updateUserTradingAccountAlpaca(request, loginUid));

        // Verify the repository interaction
        verify(userConfigurationRepository, times(1)).findUserConfigurationByUsers_LoginUid(loginUid);
        verify(userConfigurationRepository, times(0)).save(any(UserConfiguration.class));  // Save should not be called
    }

    @Test
    void testUpdateUserTradingAccountAlpaca_ThrowsExceptionWhenKeyOrSecretIsNullOrEmpty() {
        // Arrange
        UserTradingAccountAlpacaRequest request = new UserTradingAccountAlpacaRequest(null, "secret", true);
        String loginUid = "testLoginUid";

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userConfigurationService.updateUserTradingAccountAlpaca(request, loginUid));

        // Verify that findUserConfigurationByUsers_LoginUid was not called
        verify(userConfigurationRepository, times(0)).findUserConfigurationByUsers_LoginUid(loginUid);
        // Verify that updateUserConfiguration was not called
        verify(userConfigurationRepository, times(0)).save(any(UserConfiguration.class));
    }

    @Test
    void testUpdateUserConfiguration_NullDTO() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> userConfigurationService.updateUserConfiguration(null));
        verify(userConfigurationRepository, times(0)).save(any(UserConfiguration.class));
    }

    @Test
    void testIsUserTradingAccountActive() {
        // Arrange
        String loginUid = "testLoginUid";
        userConfiguration.setIsActiveTradingAccount(true);
        when(userConfigurationRepository.findUserConfigurationByUsers_LoginUid(loginUid)).thenReturn(Optional.of(userConfiguration));
        when(jwtAuthenticationToken.getName()).thenReturn(loginUid); // Ensure that the loginUid is returned from the mock

        // Act
        Map<String, Boolean> result = userConfigurationService.isUserTradingAccountActive();

        // Assert
        assertTrue(result.get("isActive"));
        verify(userConfigurationRepository, times(1)).findUserConfigurationByUsers_LoginUid(loginUid);
    }

    @Test
    void testIsUserTradingAccountActive_NotFound() {
        // Arrange
        String loginUid = "testLoginUid";
        when(userConfigurationRepository.findUserConfigurationByUsers_LoginUid(loginUid)).thenReturn(Optional.empty());

        // Act
        Boolean result = userConfigurationService.isUserTradingAccountActive().isEmpty();

        // Assert
        assertFalse(result);
        verify(userConfigurationRepository, times(1)).findUserConfigurationByUsers_LoginUid(loginUid);
    }

    @Test
    void testDeleteUserConfiguration() {
        // Arrange
        Long userConfigurationId = 1L;
        when(userConfigurationRepository.findById(userConfigurationId)).thenReturn(Optional.of(userConfiguration));

        // Act
        userConfigurationService.deleteUserConfiguration(userConfigurationId);

        // Assert
        verify(userConfigurationRepository, times(1)).findById(userConfigurationId);
        verify(userConfigurationRepository, times(1)).delete(userConfiguration);
    }

    @Test
    void testDeleteUserConfiguration_ThrowsExceptionWhenNotFound() {
        // Arrange
        Long userConfigurationId = 1L;
        when(userConfigurationRepository.findById(userConfigurationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userConfigurationService.deleteUserConfiguration(userConfigurationId));
        verify(userConfigurationRepository, times(1)).findById(userConfigurationId);
        verify(userConfigurationRepository, times(0)).delete(any(UserConfiguration.class));
    }

}
