package com.tradingbot.dotty.Services;

import com.tradingbot.dotty.models.UserConfiguration;
import com.tradingbot.dotty.models.Users;
import com.tradingbot.dotty.models.dto.UserConfigurationDTO;
import com.tradingbot.dotty.models.dto.UsersDTO;
import com.tradingbot.dotty.repositories.UsersRepository;
import com.tradingbot.dotty.serviceImpls.UsersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTests {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UsersServiceImpl usersService;

    private Users user;
    private UsersDTO usersDTO;
    private UserConfiguration userConfiguration;
    private UserConfigurationDTO userConfigurationDTO;

    @BeforeEach
    void setUp() {
        // Initialize UserConfiguration
        userConfiguration = new UserConfiguration();
        userConfigurationDTO = new UserConfigurationDTO();

        // Initialize Users
        user = new Users();
        user.setUserId(1L);
        user.setFirstName("Joe");
        user.setLastName("Doe");
        user.setEmailAddress("test@example.com");
        user.setNickname("joed");
        user.setLoginUid("login123");
        user.setPictureUrl("http://example.com/picture.jpg");
        user.setUserConfiguration(userConfiguration);
        user.setCreatedAt(LocalDateTime.now().minusDays(1));
        user.setUpdatedAt(LocalDateTime.now());
        user.setVersion(1);

        // Initialize UsersDTO
        usersDTO = new UsersDTO();
        usersDTO.setUserId(1L);
        usersDTO.setFirstName("John");
        usersDTO.setLastName("Doe");
        usersDTO.setEmailAddress("test@example.com");
        usersDTO.setNickname("Joed");
        usersDTO.setLoginUid("login123");
        usersDTO.setPictureUrl("http://example.com/picture.jpg");
        usersDTO.setUserConfigurationDTO(userConfigurationDTO);
        usersDTO.setCreatedAt(LocalDateTime.now().minusDays(1));
        usersDTO.setUpdatedAt(LocalDateTime.now());
        usersDTO.setVersion(1);

        // Set up common stubs with lenient
        lenient().when(modelMapper.map(user, UsersDTO.class)).thenReturn(usersDTO);
        lenient().when(modelMapper.map(usersDTO, Users.class)).thenReturn(user);
    }

    @Test
    void testGetUsers() {
        // Arrange
        lenient().when(usersRepository.findAll()).thenReturn(List.of(user));

        // Act
        List<UsersDTO> result = usersService.getUsers();

        // Assert
        assertEquals(1, result.size());
        assertEquals(usersDTO, result.get(0));
        verify(usersRepository, times(1)).findAll();
    }

    @Test
    void testGetUser() {
        // Arrange
        lenient().when(usersRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        Optional<UsersDTO> result = usersService.getUser(1L);

        // Assert
        assertEquals(Optional.of(usersDTO), result);
        verify(usersRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserByEmail() {
        // Arrange
        lenient().when(usersRepository.findByEmailAddress("test@example.com")).thenReturn(Optional.of(user));

        // Act
        Optional<UsersDTO> result = usersService.getUserByEmail("test@example.com");

        // Assert
        assertEquals(Optional.of(usersDTO), result);
        verify(usersRepository, times(1)).findByEmailAddress("test@example.com");
    }

    @Test
    void testGetUserByUserConfigurationId() {
        // Arrange
        lenient().when(usersRepository.findByUserConfiguration_UserConfigurationId(1L)).thenReturn(Optional.of(user));

        // Act
        Optional<UsersDTO> result = usersService.getUserByUserConfigurationId(1L);

        // Assert
        assertEquals(Optional.of(usersDTO), result);
        verify(usersRepository, times(1)).findByUserConfiguration_UserConfigurationId(1L);
    }

    @Test
    void testInsertUser() {
        // Arrange
        lenient().when(usersRepository.save(any(Users.class))).thenReturn(user);

        // Act
        Optional<UsersDTO> result = usersService.insertUser(usersDTO);

        // Assert
        assertEquals(Optional.of(usersDTO), result);
        verify(usersRepository, times(1)).save(any(Users.class));
    }

    @Test
    void testUpdateUser() {
        // Arrange
        lenient().when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        lenient().when(usersRepository.save(any(Users.class))).thenReturn(user);

        // Act
        Optional<UsersDTO> result = usersService.updateUser(usersDTO);

        // Assert
        assertEquals(Optional.of(usersDTO), result);
        verify(usersRepository, times(1)).findById(1L);
        verify(usersRepository, times(1)).save(any(Users.class));
    }

    @Test
    void testDeleteUser() {
        // Arrange
        lenient().when(usersRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        usersService.deleteUser(1L);

        // Assert
        verify(usersRepository, times(1)).findById(1L);
        verify(usersRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUserNotFound() {
        // Arrange
        lenient().when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> usersService.deleteUser(1L));
        verify(usersRepository, times(1)).findById(1L);
        verify(usersRepository, never()).delete(any(Users.class));
    }
}